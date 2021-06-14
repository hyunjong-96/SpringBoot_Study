package com.security.springbootsecurityjwt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.security.springbootsecurityjwt.dto.LoginReqDto;
import com.security.springbootsecurityjwt.dto.RegistrationReqDto;
import com.security.springbootsecurityjwt.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserController {
	private final UserService userService;

	@PostMapping("/registration")//회원가입
	public ResponseEntity<Void> registration(@RequestBody RegistrationReqDto registrationReqDto){
		userService.registration(registrationReqDto);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/login")//로그인
	public ResponseEntity<Void> login(@RequestBody LoginReqDto loginReqDto){
		userService.login(loginReqDto);
		return ResponseEntity.ok().build();
	}
}
