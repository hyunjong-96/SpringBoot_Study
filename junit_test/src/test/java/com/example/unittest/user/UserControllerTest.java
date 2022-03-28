package com.example.unittest.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.example.unittest.user.dto.UserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// @WebAppConfiguration
@WebMvcTest(UserController.class)
class UserControllerTest {
	@MockBean
	private UserService userService;
	@Autowired
	private WebApplicationContext webApplicationContext;
	@Autowired
	private MockMvc mockMvc;
	private ObjectMapper objectMapper;

	@BeforeEach
	public void init() {
		objectMapper = new ObjectMapper();
		// this.mockMvc = MockMvcBuilders
		// 	// .webAppContextSetup(webApplicationContext)
		// 	// .standaloneSetup()
		// 	// .addFilters(new CharacterEncodingFilter("UTF-8", true))
		// 	.
		// 	.build();
	}

	@Test
	void saveUser() throws Exception {
		//given
		String path = "/api/user";
		UserDto request = UserDto.builder()
			.name("테스터")
			.age(27)
			.role(Role.TRAINER)
			.build();
		// User saveUser = new User(1L, "테스터", 27, Role.TRAINER);
		given(userService.saveUser(any())).willReturn(request);

		//when
		ResultActions result = mockMvc.perform(post(path)
			.content(objectMapper.writeValueAsBytes(request))
			.contentType(MediaType.APPLICATION_JSON));

		//then
		result
			.andExpect(status().isOk())
			.andDo(print());
	}

	@Test
	void userInfo() throws Exception {
		UserDto request = UserDto.builder()
			.name("테스터")
			.age(27)
			.role(Role.TRAINER)
			.build();
		String content = objectMapper.writeValueAsString(request);
		given(userService.getUserInfo(1L)).willReturn(request);
		ResultActions result = mockMvc.perform(get("/api/user/1")
			.contentType(MediaType.APPLICATION_JSON)
			.content(content));

		result.andExpect(status().isOk())
			.andDo(print());
	}
}