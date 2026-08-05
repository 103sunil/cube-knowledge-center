package com.example.cube;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class CubeApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(CubeApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        // Required when deploying the WAR to an external container such as
        // Oracle WebLogic instead of running the embedded server.
        return builder.sources(CubeApplication.class);
    }
}
