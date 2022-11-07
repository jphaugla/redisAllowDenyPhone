package com.redis.allowDeny;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AllowDenyRedisRestApplication {

	public static void main(String[] args) {

		SpringApplication app = new SpringApplication(AllowDenyRedisRestApplication.class);
		app.run(args);
	}

}
