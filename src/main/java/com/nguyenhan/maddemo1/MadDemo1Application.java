package com.nguyenhan.maddemo1;

import com.nguyenhan.maddemo1.constants.AlarmEnum;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "customAuditorAware")
public class MadDemo1Application {

    public static void main(String[] args) {
        SpringApplication.run(MadDemo1Application.class, args);
    }

}
