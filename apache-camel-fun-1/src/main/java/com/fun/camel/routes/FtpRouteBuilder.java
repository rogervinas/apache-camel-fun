package com.fun.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class FtpRouteBuilder extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:ftp")
                .log("No route no fun!");
    }
}
