package org.example.testmodule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.example.testmodule")
public class TestModuleApplication {

    public static void main(String[] args) {
        SpringApplication.run(org.example.testmodule.TestModuleApplication.class, args);
    }
}
