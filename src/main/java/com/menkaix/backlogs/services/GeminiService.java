package com.menkaix.backlogs.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.cloud.aiplatform.v1.*;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Value;
import com.google.protobuf.util.JsonFormat;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Deprecated
@Service
public class GeminiService {

	private static Logger logger = LoggerFactory.getLogger(GeminiService.class);
	
	@Autowired
	private Environment env ;

	// Passes the provided text input to the Gemini model and returns the text-only
	// response.
	// For the specified textPrompt, the model returns a list of possible store
	// names.
	/*
	public String textInput(String textPrompt) throws IOException {

		String projectId = env.getProperty("gcp.config.project");
		String location = env.getProperty("gcp.config.location");
		String modelName = env.getProperty("gemini.config.model");
		
		
		// Initialize client that will be used to send requests. This client only needs
		// to be created once, and can be reused for multiple requests.
		
		try {

			VertexAI vertexAI = new VertexAI(projectId, location) ;

			String output;
			GenerativeModel model = new GenerativeModel(modelName, vertexAI);

			GenerationConfig config = GenerationConfig.newBuilder()
					.setMaxOutputTokens(1024)
					.setTemperature(0.2f)
					.build();

			GenerateContentResponse response = model.generateContent(textPrompt, config);
			output = ResponseHandler.getText(response);


			return output;
		} catch (IOException e) {

			logger.error(e.getClass()+": "+e.getMessage());

			return e.getMessage() ;
		}
	}
*/



	// Use Codey for Code Generation to generate a code function
	public String predictFunction(String prompt)	throws IOException {


		String project = env.getProperty("gcp.config.project");
		String location = env.getProperty("gcp.config.location");
		String model = env.getProperty("gemini.config.model");
		String publisher = env.getProperty("gemini.config.publisher");

		//TODO parametrize this
		String parameters = "{\n" + "  " +
				"\"temperature\": 0.5,\n" + " " +
				" \"maxOutputTokens\": 256\n" + "}";


		String instance = String.format("{ \"prefix\": \"%s\"}", prompt) ;

		final String endpoint = String.format("%s-aiplatform.googleapis.com:443", location);
		PredictionServiceSettings predictionServiceSettings =
				PredictionServiceSettings.newBuilder().setEndpoint(endpoint).build();

		// Initialize client that will be used to send requests. This client only needs to be created
		// once, and can be reused for multiple requests.
		try {

			PredictionServiceClient predictionServiceClient =
					PredictionServiceClient.create(predictionServiceSettings);

			EndpointName endpointName =
					EndpointName.ofProjectLocationPublisherModelName(project, location, publisher, model);

			Value instanceValue = stringToValue(instance);
			List<Value> instances = new ArrayList<>();
			instances.add(instanceValue);

			Value parameterValue = stringToValue(parameters);

			//PredictResponse predictResponse = predictionServiceClient.predict(endpointName, instances, parameterValue);



			PredictResponse predictResponse = predictionServiceClient.predict(endpointName, instances, parameterValue);

			System.out.println("Predict Response");
			System.out.println(predictResponse);


			List<Value> predictions = predictResponse.getPredictionsList() ;

			String tmp = "" ;

			if(predictResponse.getPredictionsCount()>0){
				tmp += predictResponse.getPredictions(0).getStructValue().getFieldsOrThrow("content").getStringValue();
			}

			String [] tokens = tmp.split("\n") ;

			String ans =  "" ;

			for (String token: tokens ) {
				if(!token.startsWith("```")){
					ans += token ;
				}
			}

			return ans ;


		} catch (IOException e) {
			logger.error("IOException: "+e.getMessage());
			e.printStackTrace();
			return e.getMessage();
		}
	}

	// Convert a Json string to a protobuf.Value
	static Value stringToValue(String value) throws InvalidProtocolBufferException {
		Value.Builder builder = Value.newBuilder();
		JsonFormat.parser().merge(value, builder);
		return builder.build();
	}

}
