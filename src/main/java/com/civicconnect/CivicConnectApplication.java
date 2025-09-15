package com.civicconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.civicconnect.model")
@EnableJpaRepositories("com.civicconnect.repository")
public class CivicConnectApplication {

	public static void main(String[] args) {
		SpringApplication.run(CivicConnectApplication.class, args);
	}

}
