package com.example.unittest.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.unittest.user.dto.UserDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserController {
	private final UserService userService;

	@PostMapping("/transaction-test")
	public void test(UserDto userDto){
		userService.repetitionUserSave(userDto);
	}

	@PostMapping
	public ResponseEntity<UserDto> saveUser(@RequestBody UserDto userDto){
		UserDto result = userService.saveUser(userDto);
		return ResponseEntity.ok().body(result);
	}

	@GetMapping("/{userId}")
	public ResponseEntity<UserDto> userInfo(
		@PathVariable(value = "userId")Long userId){
		UserDto result = userService.getUserInfo(userId);
		return ResponseEntity.ok().body(result);
	}

}
