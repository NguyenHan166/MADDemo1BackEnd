package com.nguyenhan.maddemo1;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "customAuditorAware")
@OpenAPIDefinition
public class MadDemo1Application {

    public static void main(String[] args) {
        SpringApplication.run(MadDemo1Application.class, args);
    }

}
