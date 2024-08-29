package com.menkaix.backlogs.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.Deflater;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

import com.menkaix.backlogs.entities.Diagram;
import com.menkaix.backlogs.repositories.DiagramRepository;
import com.menkaix.backlogs.utilities.PlantUMLEncoder;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.sourceforge.plantuml.SourceStringReader;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.net.URLEncoder;

@Controller
public class DiagramController {

        @Autowired
        private DiagramRepository diagramRepository;

        @GetMapping(path = "/diagram/{name}", produces = "image/png")
        public @ResponseBody byte[] getImage(@PathVariable("name") String name) {
                /*
                 * String source = "@startuml\n";
                 * source += "Bob -> Alice : hello Static\n";
                 * source += "@enduml\n";
                 */

                String source = "Bob -> Alice : hello";

                List<Diagram> diagrams = diagramRepository.findByName(name);

                for (Diagram diagram2 : diagrams) {
                        source = diagram2.getDefinition() ;
                        break ;
                }

                String encodedString = PlantUMLEncoder.toHex(source);

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

}
