package com.menkaix.backlogs.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

import com.menkaix.backlogs.entities.Diagram;
import com.menkaix.backlogs.repositories.DiagramRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.sourceforge.plantuml.SourceStringReader;

@Controller
public class DiagramController {



    @GetMapping(path = "/diagram/{name}", produces = "image/png")
    public @ResponseBody byte[] getImage(@PathVariable("name") String name) {

        OutputStream pngStream = null;

        String source = "@startuml\n";
        source += "Bob -> Alice : hello\n";
        source += "@enduml\n";

        SourceStringReader reader = new SourceStringReader(source);

        try {
            reader.generateImage(pngStream);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.writeTo(pngStream);
            return baos.toByteArray();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

}
