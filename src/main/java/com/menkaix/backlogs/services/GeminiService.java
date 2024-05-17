package com.menkaix.backlogs.services;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;

@Service
public class GeminiService {

	private static Logger logger = LoggerFactory.getLogger(GeminiService.class);
	
	@Autowired
	private Environment env ;

	// Passes the provided text input to the Gemini model and returns the text-only
	// response.
	// For the specified textPrompt, the model returns a list of possible store
	// names.
	public String textInput(String textPrompt) throws IOException {

		String projectId = env.getProperty("gcp.config.project");
		String location = env.getProperty("gcp.config.location");
		String modelName = env.getProperty("gemini.config.model");
		
		
		// Initialize client that will be used to send requests. This client only needs
		// to be created once, and can be reused for multiple requests.
		
		try (VertexAI vertexAI = new VertexAI(projectId, location)) {
			String output;
			GenerativeModel model = new GenerativeModel(modelName, vertexAI);

			GenerateContentResponse response = model.generateContent(textPrompt);
			output = ResponseHandler.getText(response);
			return output;
		}
	}

}
