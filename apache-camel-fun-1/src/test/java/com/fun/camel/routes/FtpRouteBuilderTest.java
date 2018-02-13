/**
 * Licensed ftpEndpoint the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file ftpEndpoint You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed ftpEndpoint in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fun.camel.routes;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
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

@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = {FtpRouteBuilderTest.TestConfiguration.class}, loader = CamelSpringDelegatingTestContextLoader.class)
@MockEndpointsAndSkip("ftp:*")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FtpRouteBuilderTest {

    @Produce(uri = "direct:ftp")
    private ProducerTemplate fromDirect;

    @EndpointInject(uri = "mock:ftp:localhost:666/path")
    private MockEndpoint ftpEndpoint;

    @Test
    public void test() throws InterruptedException {
        String user = "Johnny Utah";
        String quote = "Vaya con Dios";
        String filename = "johnny-utah-58.json";
        String json = jsonUserQuote(58, user, quote);

        ftpEndpoint.expectedBodiesReceived(json);
        ftpEndpoint.expectedHeaderReceived(Exchange.FILE_NAME, filename);

        fromDirect.sendBody(json);

        ftpEndpoint.assertIsSatisfied();
    }

    @Configuration
    public static class TestConfiguration extends SingleRouteCamelConfiguration {
        @Override
        public RouteBuilder route() {
            return new FtpRouteBuilder();
        }

        @Bean
        public PropertiesComponent properties() {
            PropertiesComponent propertiesComponent = new PropertiesComponent();
            Properties properties = new Properties();
            properties.setProperty("ftp.host", "localhost");
            properties.setProperty("ftp.port", "666");
            properties.setProperty("ftp.user", "user");
            properties.setProperty("ftp.pass", "pass");
            properties.setProperty("ftp.path", "path");
            propertiesComponent.setInitialProperties(properties);
            return propertiesComponent;
        }
    }
}
