package com.fun.camel.routes;

import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fun.camel.model.UserQuote;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.s3.S3Constants;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration;
import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.apache.camel.test.spring.MockEndpointsAndSkip;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.Properties;

import static org.mockito.Mockito.mock;

@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = {S3RouteBuilderTest.TestConfiguration.class}, loader = CamelSpringDelegatingTestContextLoader.class)
@MockEndpointsAndSkip("aws-s3:*")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class S3RouteBuilderTest {

    @Produce(uri = "direct:s3")
    private ProducerTemplate fromDirect;

    @EndpointInject(uri = "mock:aws-s3:test")
    private MockEndpoint s3Endpoint;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void should_send_once_when_no_exception() throws Exception {

        int id = 174;
        String user = "Leia Organa";
        String quote = "Help me, Obi-Wan Kenobi. You’re my only hope";
        UserQuote userQuote = new UserQuote(id, user, quote);

        String json = objectMapper.writeValueAsString(userQuote);
        String key = "Leia Organa - 174";

        s3Endpoint.expectedMessagesMatches(exchange -> exchange.getIn().getBody() instanceof String);
        s3Endpoint.expectedBodiesReceived(json);
        s3Endpoint.expectedHeaderReceived(S3Constants.KEY, key);

        fromDirect.sendBody(userQuote);

        s3Endpoint.assertIsSatisfied();
    }

    @Configuration
    public static class TestConfiguration extends SingleRouteCamelConfiguration {
        @Override
        public RouteBuilder route() {
            return new S3RouteBuilder();
        }

        @Bean
        public PropertiesComponent properties() {
            PropertiesComponent propertiesComponent = new PropertiesComponent();
            Properties properties = new Properties();
            properties.put("s3.bucket", "test");
            properties.put("s3.endpoint", "http://s3.fake.com");
            propertiesComponent.setInitialProperties(properties);
            return propertiesComponent;
        }

        @Bean("s3Client")
        public AmazonS3 createAmazonS3Client() {
            return mock(AmazonS3.class);
        }
    }
}
