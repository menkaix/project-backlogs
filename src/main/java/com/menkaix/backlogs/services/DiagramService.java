package com.menkaix.backlogs.services;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.menkaix.backlogs.entities.Diagram;
import com.menkaix.backlogs.repositories.DiagramRepository;
import com.menkaix.backlogs.utilities.PlantUMLEncoder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Service
public class DiagramService {

	@Autowired
	private DiagramRepository diagramRepository;

	public String encodedDiagramDefinition(String name) {

		String source = "Bob -> Alice : hello";

		List<Diagram> diagrams = diagramRepository.findByName(name);

		for (Diagram diagram2 : diagrams) {
			source = diagram2.getDefinition();
			break;
		}

		String encodedString = PlantUMLEncoder.toHex(source);

		return encodedString;
	}
	
	public byte[] getDiagramPNG(String encodedString) {
		

        OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();

        Request request = new Request.Builder()
                        .url("http://www.plantuml.com/plantuml/png/~h" + encodedString)
                        .method("GET", null)
                        .build();
        try {
                Response response = client.newCall(request).execute();

                return response.body().bytes();

        } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
        }
		
	}

	public Diagram updateDefinition(String name, String data) {
		
		List<Diagram> diagrams = diagramRepository.findByName(name);

		for (Diagram diagram2 : diagrams) {
			
			diagram2.setDefinition(data);
			
			Diagram saved = diagramRepository.save(diagram2) ;
			
			return saved ;
		}
		
		
		return null;
	}

	public String getDiagramDefinition(String name) {

		List<Diagram> diagrams = diagramRepository.findByName(name);

		for (Diagram diagram2 : diagrams) {
			return diagram2.getDefinition();
		}
		
		return "" ;
	}

}
