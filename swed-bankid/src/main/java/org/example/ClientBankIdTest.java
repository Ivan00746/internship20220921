package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.logging.Logger;

@SpringBootApplication
public class ClientBankIdTest {

    private static final Logger log = Logger.getLogger(ClientBankIdTest.class.getName());

    public static void main(String[] args) {
        log.info("Start main...");
        ApplicationContext ctx = SpringApplication.run(ClientBankIdTest.class, args);
        log.info("Finish main...");
    }
}

