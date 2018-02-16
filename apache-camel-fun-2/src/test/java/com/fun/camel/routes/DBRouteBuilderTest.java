package com.fun.camel.routes;

import com.fun.camel.model.UserQuote;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
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

import javax.sql.DataSource;

import static org.mockito.Mockito.mock;

@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = {DBRouteBuilderTest.TestConfiguration.class}, loader = CamelSpringDelegatingTestContextLoader.class)
@MockEndpointsAndSkip("sql:*")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DBRouteBuilderTest {

    @Produce(uri = "direct:db")
    private ProducerTemplate fromDirect;

    @EndpointInject(uri = "mock:sql:noop")
    private MockEndpoint dbEndpoint;

    @Test
    public void should_send_once_when_no_exception() throws Exception {

        int id = 87;
        String user = "Darth Vader";
        String quote = "I find your lack of faith disturbing";
        UserQuote userQuote = new UserQuote(id, user, quote);

        dbEndpoint.expectedBodiesReceived("INSERT INTO user_quotes (id, name, quote) VALUES (:?id, :?name, :?quote)");
        dbEndpoint.expectedHeaderReceived("id", id);
        dbEndpoint.expectedHeaderReceived("name", user);
        dbEndpoint.expectedHeaderReceived("quote", quote);

        fromDirect.sendBody(userQuote);

        dbEndpoint.assertIsSatisfied();
    }

    @Configuration
    public static class TestConfiguration extends SingleRouteCamelConfiguration {
        @Override
        public RouteBuilder route() {
            return new DBRouteBuilder();
        }

        @Bean("dataSource")
        public DataSource createDataSource() {
            return mock(DataSource.class);
        }
    }
}
