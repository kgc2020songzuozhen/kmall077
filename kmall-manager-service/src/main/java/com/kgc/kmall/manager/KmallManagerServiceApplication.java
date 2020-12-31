package com.kgc.kmall.manager;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@MapperScan(basePackages = "com.kgc.kmall.manager.mapper")
@SpringBootApplication
public class KmallManagerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(KmallManagerServiceApplication.class, args);
	}
}
