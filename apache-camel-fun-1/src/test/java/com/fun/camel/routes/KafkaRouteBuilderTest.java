/**
 * Licensed kafkaEndpoint the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file kafkaEndpoint You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed kafkaEndpoint in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fun.camel.routes;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
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

import static com.fun.camel.helpers.JSONHelper.jsonUserQuote;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = {KafkaRouteBuilderTest.TestConfiguration.class}, loader = CamelSpringDelegatingTestContextLoader.class)
@MockEndpointsAndSkip("kafka:*")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class KafkaRouteBuilderTest {

    @Produce(uri = "direct:kafka")
    private ProducerTemplate fromDirect;

    @EndpointInject(uri = "mock:kafka:topic")
    private MockEndpoint kafkaEndpoint;

    @Test
    public void should_send_once_when_no_exception() throws InterruptedException {

        String user = "Bodhi";
        String quote = "Fear causes hesitation, and hesitation will cause your worst fears to come true";
        String json = jsonUserQuote(195, user, quote);

        kafkaEndpoint.expectedBodiesReceived(json);
        kafkaEndpoint.expectedHeaderReceived(KafkaConstants.KEY, user);

        fromDirect.sendBody(json);

        kafkaEndpoint.assertIsSatisfied();
    }

    @Test(expected = RuntimeException.class)
    public void should_fail_when_exception() throws Throwable {

        String user = "Johnny Utah";
        String quote = "He's not coming back";
        String json = jsonUserQuote(36, user, quote);

        RuntimeException unexpectedException = new RuntimeException("I'll see you in hell, Johnny!");
        kafkaEndpoint.whenAnyExchangeReceived(exchange -> { throw unexpectedException; });
        kafkaEndpoint.expectedBodiesReceived(json);

        try {
            fromDirect.sendBody(json);
        } catch(CamelExecutionException camelException) {
            assertThat(camelException.getCause())
                    .isEqualTo(unexpectedException);

            kafkaEndpoint.assertIsSatisfied();

            throw camelException.getCause();
        }
    }

    @Configuration
    public static class TestConfiguration extends SingleRouteCamelConfiguration {
        @Override
        public RouteBuilder route() {
            return new KafkaRouteBuilder();
        }

        @Bean
        public PropertiesComponent properties() {
            PropertiesComponent propertiesComponent = new PropertiesComponent();
            Properties properties = new Properties();
            properties.setProperty("kafka.topic", "topic");
            properties.setProperty("kafka.host", "localhost");
            properties.setProperty("kafka.port", "666");
            propertiesComponent.setInitialProperties(properties);
            return propertiesComponent;
        }
    }
}
