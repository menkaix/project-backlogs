package com.menkaix.backlogs.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.bind.annotation.CrossOrigin;

@ComponentScan(basePackages = {
		"com.menkaix.backlogs.services",
		"com.menkaix.backlogs.controllers"
})
@EnableMongoRepositories("com.menkaix.backlogs.repositories")
@CrossOrigin
@SpringBootApplication
public class AccessingDataMongodbApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccessingDataMongodbApplication.class, args);
	}


}
