package com.fun.camel;

import org.apache.camel.spring.boot.CamelSpringBootApplicationController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		// We need to prevent Spring to shutdown. Not needed if it was a web application.
		new SpringApplication(Application.class)
				.run(args)
				.getBean(CamelSpringBootApplicationController.class)
				.run();
	}
}
