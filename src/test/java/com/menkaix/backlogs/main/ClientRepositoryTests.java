package com.menkaix.backlogs.main;
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

import java.util.ArrayList;
import java.util.Optional;

import com.menkaix.backlogs.services.applicatif.DataAccessService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.menkaix.backlogs.entities.Project;
import com.menkaix.backlogs.repositories.ProjectRepository;

@SpringBootTest
public class ClientRepositoryTests {

	@MockBean
	ProjectRepository projectRepository;

	@Autowired
	DataAccessService service;

	@Autowired
	ProjectService projectService ;

	@BeforeEach
	public void configureTest() {

		ArrayList<Project> prjs = new ArrayList<>();
		Project project = new Project("test");
		project.id = "abcd1234";
		project.code = "TST";
		prjs.add(project);

		Mockito.when(projectRepository.findByName("test")).thenReturn(prjs);
		Mockito.when(projectRepository.findByCode("TST")).thenReturn(prjs);

		Optional<Project> oPrj = Optional.of(project);

		Mockito.when(projectRepository.findById("abcd1234")).thenReturn(oPrj);

	}

	@Test
	public void ShouldFindProjectByName() {

		Project prj = service.findProject("test");

		Assert.assertEquals("abcd1234", prj.id);
	}

	@Test
	public void ShouldMergeLists() {

		ArrayList<String> initial = new ArrayList<>() ;
		ArrayList<String> adendum = new ArrayList<>() ;

		adendum.add("bla") ;

		projectService.merge(initial, adendum);

		Assert.assertEquals(1, initial.size());
	}

	@Test
	public void ShouldFindProjectByCode() {

		Project prj = service.findProject("TST");

		Assert.assertEquals("abcd1234", prj.id);
	}

	@Test
	public void ShouldFindProjectById() {

		Project prj = service.findProject("abcd1234");

		Assert.assertEquals("TST", prj.code);
	}

}
