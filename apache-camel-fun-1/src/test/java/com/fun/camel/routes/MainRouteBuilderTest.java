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

import org.apache.camel.CamelContext;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.fun.camel.helpers.JSONHelper.jsonUserQuote;
import static com.fun.camel.helpers.XMLHelper.xmlUserQuote;
import static com.fun.camel.helpers.XMLHelper.xmlUserQuotes;
import static com.fun.camel.helpers.ZIPHelper.zip;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = {MainRouteBuilderTest.TestConfiguration.class}, loader = CamelSpringDelegatingTestContextLoader.class)
@UseAdviceWith
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MainRouteBuilderTest {

    @Produce(uri = "direct:file")
    private ProducerTemplate fromFile;

    @EndpointInject(uri = "mock:direct:ftp")
    private MockEndpoint ftpEndpoint;

    @EndpointInject(uri = "mock:direct:kafka")
    private MockEndpoint kafkaEndpoint;

    @Autowired
    private CamelContext camelContext;

    private Map<String, Object> headers;

    @Before
    public void before() throws Exception {
        headers = new HashMap<>();
        headers.put(Exchange.FILE_NAME, "file.zip");
        headers.put(Exchange.FILE_LENGTH, System.currentTimeMillis() % 10_000);

        camelContext.getRouteDefinitions().get(0).adviceWith(camelContext, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("direct:file");
                mockEndpointsAndSkip("direct:kafka", "direct:ftp");
            }
        });
        camelContext.start();
    }

    @Test
    public void should_process_file_with_no_UserQuotes() throws Exception {

        kafkaEndpoint.expectedMessageCount(0);
        ftpEndpoint.expectedMessageCount(0);

        String xml = xmlUserQuotes();
        byte[] zip = zip("file0.xml", xml);
        fromFile.sendBodyAndHeaders(zip, headers);

        kafkaEndpoint.assertIsSatisfied();
        ftpEndpoint.assertIsSatisfied();
    }

    @Test
    public void should_process_file_with_one_UserQuote() throws Exception {

        String name = "Sam";
        String surname = "Trautman";
        String quote = "I didn't come to rescue Rambo from you";
        int id = 13;
        String json = jsonUserQuote(id, name + " " + surname, quote);

        kafkaEndpoint.expectedBodiesReceived(json);
        ftpEndpoint.expectedBodiesReceived(json);

        String xml = xmlUserQuotes(xmlUserQuote(id, name, surname, quote));
        byte[] zip = zip("file1.xml", xml);
        fromFile.sendBodyAndHeaders(zip, headers);

        kafkaEndpoint.assertIsSatisfied();
        ftpEndpoint.assertIsSatisfied();
    }

    @Test
    public void should_process_file_with_three_UserQuotes() throws Exception {

        String name1 = "Sam";
        String surname1 = "Trautman";
        String quote1 = "I didn't come to rescue Rambo from you. I came here to rescue you from him";
        int id1 = 25;
        String json1 = jsonUserQuote(id1, name1 + " " + surname1, quote1);

        String name2 = "John";
        String surname2 = "Rambo";
        String quote2 = "Don't push it or I'll give you a war you won't believe";
        int id2 = 17;
        String json2 = jsonUserQuote(id2, name2 + " " + surname2, quote2);

        String name3 = "Will";
        String surname3 = "Teasle";
        String quote3 = "Now don't give me any of that crap Trautman";
        int id3 = 183;
        String json3 = jsonUserQuote(id3, name3 + " " + surname3, quote3);

        kafkaEndpoint.expectedBodiesReceivedInAnyOrder(json1, json2, json3);
        ftpEndpoint.expectedBodiesReceivedInAnyOrder(json1, json2, json3);

        String xml = xmlUserQuotes(
                xmlUserQuote(id1, name1, surname1, quote1),
                xmlUserQuote(id2, name2, surname2, quote2),
                xmlUserQuote(id3, name3, surname3, quote3)
        );
        byte[] zip = zip("file3.xml", xml);
        fromFile.sendBodyAndHeaders(zip, headers);

        kafkaEndpoint.assertIsSatisfied();
        ftpEndpoint.assertIsSatisfied();
    }

    @Test(expected = RuntimeException.class)
    public void should_fail_when_exception_on_kafka() throws Throwable {

        String name = "Will";
        String surname = "Teasle";
        String quote = "Are you telling me that 200 of our men against your boy is a no-win situation for us?";
        int id = 148;
        String json = jsonUserQuote(id, name + " " + surname, quote);

        RuntimeException unexpectedException = new RuntimeException("You send that many, don't forget a good supply of body bags");
        kafkaEndpoint.whenAnyExchangeReceived(exchange -> { throw unexpectedException; });
        kafkaEndpoint.expectedBodiesReceived(json);
        ftpEndpoint.expectedBodiesReceived(json);

        sendAndassertCamelExceptionIsThrown(name, surname, quote, id, unexpectedException);
    }

    @Test(expected = RuntimeException.class)
    public void should_fail_when_exception_on_ftp() throws Throwable {

        String name = "Sam";
        String surname = "Trautman";
        String quote = "Well you did some pushing on your own John";
        int id = 275;
        String json = jsonUserQuote(id, name + " " + surname, quote);

        RuntimeException unexpectedException = new RuntimeException("They drew first blood, not me");
        kafkaEndpoint.expectedBodiesReceived(json);
        ftpEndpoint.whenAnyExchangeReceived(exchange -> { throw unexpectedException; });
        ftpEndpoint.expectedBodiesReceived(json);

        sendAndassertCamelExceptionIsThrown(name, surname, quote, id, unexpectedException);
    }

    private void sendAndassertCamelExceptionIsThrown(
            String name,
            String surname,
            String quote,
            int id,
            RuntimeException unexpectedException
    ) throws Throwable {
        try {
            String xml = xmlUserQuotes(xmlUserQuote(id, name, surname, quote));
            byte[] zip = zip("fileX.xml", xml);
            fromFile.sendBodyAndHeaders(zip, headers);
        } catch(CamelExecutionException camelException) {
            assertThat(camelException.getCause())
                    .isEqualTo(unexpectedException);

            kafkaEndpoint.assertIsSatisfied();
            ftpEndpoint.assertIsSatisfied();

            throw camelException.getCause();
        }
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
            properties.setProperty("file.path", "path");
            properties.setProperty("file.delay", "5000");
            propertiesComponent.setInitialProperties(properties);
            return propertiesComponent;
        }
    }
}
