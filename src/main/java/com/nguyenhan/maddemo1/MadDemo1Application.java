package com.nguyenhan.maddemo1;

import com.nguyenhan.maddemo1.constants.AlarmEnum;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MadDemo1Application {

    public static void main(String[] args) {
//        System.out.println(AlarmEnum.LICHHOC);
        SpringApplication.run(MadDemo1Application.class, args);
    }

}
