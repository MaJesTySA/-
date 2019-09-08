package com.inside;

import tk.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan(basePackages = "com.inside.mapper")
@ComponentScan(basePackages = {"com.inside", "org.n3r.idworker"})
public class Applicaiton {
    public static void main(String[] args) {
        SpringApplication.run(Applicaiton.class, args);
    }
}
