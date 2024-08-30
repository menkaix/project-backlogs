package com.menkaix.backlogs.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.menkaix.backlogs.entities.Diagram;
import com.menkaix.backlogs.services.DiagramService;

@Controller
@RequestMapping("/diagram")
public class DiagramController {

	@Autowired
	private DiagramService service;

	@GetMapping(path = "/png/{name}", produces = "image/png")
	public @ResponseBody byte[] getImage(@PathVariable("name") String name) {

		String encoded = service.encodedDiagramDefinition(name);

		return service.getDiagramPNG(encoded);

	}

	@GetMapping(path = "/plant-url/{name}", produces = "text/plain")
	public @ResponseBody String getPlantUrl(@PathVariable("name") String name) {

		String encoded = service.encodedDiagramDefinition(name);

		return "http://www.plantuml.com/plantuml/png/~h" + encoded;

	}

	@RequestMapping(path = "/update/{name}", produces = "application/json", consumes = "text/plain", method = RequestMethod.PATCH)
	public @ResponseBody Diagram updateDescription(@PathVariable("name") String name, @RequestBody String data) {

		return service.updateDefinition(name, data);

	}
	
	@RequestMapping(path = "/update-graphic/{name}", produces = "image/png", consumes = "text/plain", method = RequestMethod.PATCH)
	public @ResponseBody byte[] updateDescriptionGraphics(@PathVariable("name") String name, @RequestBody String data) {

		service.updateDefinition(name, data);
		
		String encoded = service.encodedDiagramDefinition(name);

		return service.getDiagramPNG(encoded);
		

	}

}
