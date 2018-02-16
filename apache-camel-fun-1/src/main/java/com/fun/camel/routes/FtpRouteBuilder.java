package com.fun.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class FtpRouteBuilder extends RouteBuilder {

    private final Duration retryDelay;
    private final int retryCount;

    public FtpRouteBuilder(
            @Value("ftp.retry.delay") String retryDelay,
            @Value("ftp.retry.count") int retryCount
    ) {
        this.retryDelay = Duration.parse(retryDelay);
        this.retryCount = retryCount;
    }

    @Override
    public void configure() {
        from("direct:ftp")
                .log("No route no fun!");
    }
}
