package com.example.accessingdatamongodb;
/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Example;

import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
public class ClientRepositoryTests {

	@Container
	@ServiceConnection
	static MongoDBContainer container = new MongoDBContainer("mongo:7.0.2");

	@Autowired
	ClientRepository repository;

	Client dave, oliver, carter;

	@BeforeEach
	public void setUp() {

		repository.deleteAll();

		dave = repository.save(new Client("Dave", "Matthews"));
		oliver = repository.save(new Client("Oliver August", "Matthews"));
		carter = repository.save(new Client("Carter", "Beauford"));
	}

	@Test
	public void setsIdOnSave() {

		Client dave = repository.save(new Client("Dave", "Matthews"));

		assertThat(dave.id).isNotNull();
	}

	@Test
	public void findsByLastName() {

		List<Client> result = repository.findByLastName("Beauford");

		assertThat(result).hasSize(1).extracting("firstName").contains("Carter");
	}

	@Test
	public void findsByExample() {

		Client probe = new Client(null, "Matthews");

		List<Client> result = repository.findAll(Example.of(probe));

		assertThat(result).hasSize(2).extracting("firstName").contains("Dave", "Oliver August");
	}
}
