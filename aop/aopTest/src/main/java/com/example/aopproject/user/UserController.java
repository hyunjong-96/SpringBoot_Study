package com.example.aopproject.user;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserController {
	private final UserService userService;

	@PostMapping
	public String newUser(@RequestBody NewUserDto newUserDto){
		userService.createUser(newUserDto);
		return "생성완료";
	}

	@GetMapping
	public String allUser(){
		return userService.allUser();
	}

	@GetMapping("/list")
	public List<User> getUserList(){
		return userService.getDataAll();
	}

	@PostMapping("/update")
	public void updateUser(){
		List<User> userList = userService.getUserList();
		userService.update(userList.get(0));
	}
}
