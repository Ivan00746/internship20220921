package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class LeafletStartApp {
    public static void main(String[] args) {
        ConfigurableApplicationContext conAppContext = SpringApplication.run(LeafletStartApp.class, args);
    }
}