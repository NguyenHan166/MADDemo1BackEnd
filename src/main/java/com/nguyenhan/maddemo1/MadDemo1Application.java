package com.nguyenhan.maddemo1;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "customAuditorAware")
@EnableScheduling
@OpenAPIDefinition
public class MadDemo1Application {

    public static void main(String[] args) {
        SpringApplication.run(MadDemo1Application.class, args);
    }

}
