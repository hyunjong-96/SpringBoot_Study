package com.example.unittest.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
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
			.andExpect(jsonPath("email").value(email))
			.andDo(print());
	}

	@Test
	void modelAttributeTest() throws Exception {
		String name = "테스터";
		String age = "27";
		String password = "123123";
		String email = "test@test.com";
		ModelAttributeDto modelAttributeDto =
			new ModelAttributeDto(name, 27, password, email);
		mvc
			.perform(post("/api/test/modelattribute")
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.flashAttr("modelAttributeDto", modelAttributeDto)
				// .param("name",name)
				// .param("age", age)
				// .param("password", password)
				// .param("email", email)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("name").value(name))
			.andExpect(jsonPath("age").value(age))
			.andExpect(jsonPath("password").value(password))
			.andExpect(jsonPath("email").value(email))
			.andDo(print());
	}
}