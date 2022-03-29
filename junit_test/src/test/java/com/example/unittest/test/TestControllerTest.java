package com.example.unittest.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(TestController.class)
class TestControllerTest {
	@Autowired
	private MockMvc mvc;
	@Autowired
	private ObjectMapper objectMapper;


	@Test
	void requestBodyTest() throws Exception {
		String name = "테스터";
		long age = 27;
		String password = "123123";
		String email = "test@test.com";
		RequestBodyDto requestBodyDto
			= new RequestBodyDto(name, age, password, email);

		String content = objectMapper.writeValueAsString(requestBodyDto);
		mvc
			.perform(post("/api/test")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("name").value(name))
			.andExpect(jsonPath("age").value(age))
			.andExpect(jsonPath("email").value(email));
	}
}