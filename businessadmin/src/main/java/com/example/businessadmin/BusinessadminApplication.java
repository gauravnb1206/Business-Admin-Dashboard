package com.example.businessadmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@EnableSpringDataWebSupport
@SpringBootApplication
public class BusinessadminApplication {

	public static void main(String[] args) {
		SpringApplication.run(BusinessadminApplication.class, args);
	}


}
