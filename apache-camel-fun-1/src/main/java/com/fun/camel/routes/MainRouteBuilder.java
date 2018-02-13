package com.fun.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MainRouteBuilder extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:file")
                .log("No route no fun!");
    }
}
