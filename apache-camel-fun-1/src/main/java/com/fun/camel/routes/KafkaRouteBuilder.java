package com.fun.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class KafkaRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:kafka")
                .log("No route no fun!");
    }
}
