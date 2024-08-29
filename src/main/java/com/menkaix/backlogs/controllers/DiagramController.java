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

import java.net.URLEncoder ;

@Controller
public class DiagramController {

    @GetMapping(path = "/diagram/{name}", produces = "text/plain")
    public @ResponseBody String getImage(@PathVariable("name") String name) {
        /*
         * String source = "@startuml";
         * source += "Bob -> Alice : hello";
         * source += "@enduml";
         */

        


        String source = "Bob -> Alice : hello";

        String encodedString = PlantUMLEncoder.toHex(source);

        return encodedString;
    }

}
