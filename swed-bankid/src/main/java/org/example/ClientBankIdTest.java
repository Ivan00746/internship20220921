package org.example;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.logging.Logger;

@SpringBootApplication
public class ClientBankIdTest {
    private static ConfigurableApplicationContext context;
    private static final Logger log = Logger.getLogger(ClientBankIdTest.class.getName());

    public static void main(String[] args) {
        log.info("Start main...");
        context = SpringApplication.run(ClientBankIdTest.class, args);
        log.info("Finish main...");
    }

    public static void restart() {
        ApplicationArguments args = context.getBean(ApplicationArguments.class);
        Thread thread = new Thread(() -> {
            context.close();
            context = SpringApplication.run(ClientBankIdTest.class, args.getSourceArgs());
        });
        thread.setDaemon(false);
        thread.start();
    }
}

