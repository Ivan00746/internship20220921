package org.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
@SpringBootApplication
public class ClientBankIdTest {
    private static ConfigurableApplicationContext context;

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

