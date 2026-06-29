package com.ptit.rikkei_bank;

import com.ptit.rikkei_bank.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class RikkeiBankApplication {

	public static void main(String[] args) {
		SpringApplication.run(RikkeiBankApplication.class, args);
	}

}
