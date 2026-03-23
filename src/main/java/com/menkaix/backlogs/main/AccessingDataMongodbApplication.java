package com.menkaix.backlogs.main;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoAuditing
@ComponentScan(basePackages = {
		"com.menkaix.backlogs.services",
		"com.menkaix.backlogs.controllers",
		"com.menkaix.backlogs.services.applicatif",
		"com.menkaix.backlogs.configuration",
		"com.menkaix.backlogs.security",
		"com.menkaix.backlogs.mcptools",
		"com.menkaix.backlogs.mcp"
})
@EnableMongoRepositories("com.menkaix.backlogs.repositories")
@SpringBootApplication
public class AccessingDataMongodbApplication implements CommandLineRunner {



	public static void main(String[] args) {
		SpringApplication.run(AccessingDataMongodbApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {

	}
}
