package com.contractguard.contractguard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ContractguardApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContractguardApplication.class, args);
	}

}
