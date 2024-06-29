package com.kelompok2.remindertugas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RemindertugasApplication {

	public static void main(String[] args) {
		SpringApplication.run(RemindertugasApplication.class, args);
	}

}
