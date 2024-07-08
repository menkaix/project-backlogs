package com.menkaix.backlogs.main;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.bind.annotation.CrossOrigin;

@ComponentScan(basePackages = {
		"com.menkaix.backlogs.services",
		"com.menkaix.backlogs.controllers",
		"com.menkaix.backlogs.services.applicatif"
})
@EnableMongoRepositories("com.menkaix.backlogs.repositories")
@CrossOrigin
@SpringBootApplication
public class AccessingDataMongodbApplication implements CommandLineRunner {



	public static void main(String[] args) {
		SpringApplication.run(AccessingDataMongodbApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {

	}
}
