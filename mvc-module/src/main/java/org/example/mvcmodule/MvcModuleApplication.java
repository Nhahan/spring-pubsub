package org.example.mvcmodule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.example.mvcmodule")
public class MvcModuleApplication {

    public static void main(String[] args) {
        SpringApplication.run(MvcModuleApplication.class, args);
    }
}
