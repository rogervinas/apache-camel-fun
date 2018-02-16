package com.fun.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class S3RouteBuilder extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:s3")
                .log("No route no fun!");
    }
}
