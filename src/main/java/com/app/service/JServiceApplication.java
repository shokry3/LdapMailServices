package com.app.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAsync
public class JServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(JServiceApplication.class, args);
		//System.out.println("Spring boot Mail service application starteddddddddddddddddddddddddddd ....");
	}

}
