package com.crosska.api.socksApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.crosska.api.socksApi")
public class SocksApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocksApiApplication.class, args);
	}

}
