package com.fun.camel.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fun.camel.model.UserQuote;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration;
import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.apache.camel.test.spring.UseAdviceWith;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.Properties;

@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = {MainRouteBuilderTest.TestConfiguration.class}, loader = CamelSpringDelegatingTestContextLoader.class)
@UseAdviceWith
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MainRouteBuilderTest {

    @Produce(uri = "direct:kafka")
    private ProducerTemplate fromKafka;

    @EndpointInject(uri = "mock:direct:s3")
    private MockEndpoint s3Endpoint;

    @EndpointInject(uri = "mock:direct:db")
    private MockEndpoint dbEndpoint;

    @Autowired
    private CamelContext camelContext;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void before() throws Exception {
        camelContext.getRouteDefinitions().get(0).adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("direct:kafka");
                mockEndpointsAndSkip("direct:s3", "direct:db");
            }
        });
        camelContext.start();
    }

    @Test
    public void should_send_even_id_to_s3() throws Exception {
        UserQuote userQuote = new UserQuote(184, "Vincent Vega", "And you know what they call a... a... a Quarter Pounder with Cheese in Paris?");
        String json = objectMapper.writeValueAsString(userQuote);

        s3Endpoint.expectedBodiesReceived(userQuote);
        dbEndpoint.expectedMessageCount(0);

        fromKafka.sendBody(json);

        s3Endpoint.assertIsSatisfied();
        dbEndpoint.assertIsSatisfied();
    }

    @Test
    public void should_send_odd_id_to_db() throws Exception {
        UserQuote userQuote = new UserQuote(53, "Jules Winnfield", "They don't call it a Quarter Pounder with cheese?");
        String json = objectMapper.writeValueAsString(userQuote);

        s3Endpoint.expectedMessageCount(0);
        dbEndpoint.expectedBodiesReceived(userQuote);

        fromKafka.sendBody(json);

        s3Endpoint.assertIsSatisfied();
        dbEndpoint.assertIsSatisfied();
    }

    @Configuration
    public static class TestConfiguration extends SingleRouteCamelConfiguration {
        @Override
        public RouteBuilder route() {
            return new MainRouteBuilder();
        }

        @Bean
        public PropertiesComponent properties() {
            PropertiesComponent propertiesComponent = new PropertiesComponent();
            Properties properties = new Properties();
            properties.put("kafka.host", "localhost");
            properties.put("kafka.port", "6666");
            properties.put("kafka.topic", "test.topic");
            properties.put("kafka.groupId", "test.consumer");
            properties.put("kafka.consumersCount", "1");
            properties.put("kafka.consumerStreams", "1");
            propertiesComponent.setInitialProperties(properties);
            return propertiesComponent;
        }
    }
}
