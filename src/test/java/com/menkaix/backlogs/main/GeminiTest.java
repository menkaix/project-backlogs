package com.menkaix.backlogs.main;

import java.io.IOException;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.menkaix.backlogs.services.GeminiService;

@SpringBootTest
public class GeminiTest {
	
	@Autowired
	GeminiService geminiService ;
	
//	@Test
//	public void shouldRespond() {
//		
//		try {
//			String ans = geminiService.textInput("écris-moi un sonnet sur la vie en entreprise");
//			System.out.println(ans);
//			Assert.assertTrue(ans.length()>0);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		
//		
//		
//		
//	}

}
