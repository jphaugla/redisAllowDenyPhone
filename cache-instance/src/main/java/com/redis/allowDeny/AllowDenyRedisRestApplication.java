package com.redis.allowDeny;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AllowDenyRedisRestApplication {

	public static void main(String[] args) {

		SpringApplication app = new SpringApplication(AllowDenyRedisRestApplication.class);
		app.run(args);
	}

}
