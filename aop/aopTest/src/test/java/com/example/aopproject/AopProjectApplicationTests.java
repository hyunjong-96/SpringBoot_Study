package com.example.aopproject;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.aopproject.board.BoardService;
import com.example.aopproject.user.NewUserDto;
import com.example.aopproject.user.UserService;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class AopProjectApplicationTests {

	@Autowired
	private BoardService boardService;

	@Autowired
	private UserService userService;

	private NewUserDto user1 = new NewUserDto("test@test.com", "tester1");
	private NewUserDto user2 = new NewUserDto("test2@test.com", "tester2");

	@BeforeEach
	public void init(){
		userService.createUser(user1);
		userService.createUser(user2);
	}

	@DisplayName(value = "회원 수 비교")
	@Test
	void findAllUserTest() {
		assertThat(userService.getDataAll().size()).isEqualTo(2);
	}

}
