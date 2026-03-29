package com.welovepalace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WelovePalaceApplication {

    public static void main(String[] args) {
    io.github.cdimascio.dotenv.Dotenv dotenv = io.github.cdimascio.dotenv.Dotenv.configure().ignoreIfMissing().load();
    dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));
    SpringApplication.run(WelovePalaceApplication.class, args);
    }
}
