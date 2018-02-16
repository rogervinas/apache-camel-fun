package com.fun.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class DBRouteBuilder extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:db")
                .log("No route no fun!");
    }
}
