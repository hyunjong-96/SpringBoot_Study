package com.example.querydsl.user.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.querydsl.rutin.domain.Rutin;
import com.example.querydsl.rutin.domain.RutinRepository;
import com.example.querydsl.user.GetUserDto;
import com.example.querydsl.user.UserDto;
import com.example.querydsl.user.doamin.User;
import com.example.querydsl.user.doamin.UserQueryRepository;
import com.example.querydsl.user.doamin.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class UserController {
	private final UserRepository userRepository;
	private final UserQueryRepository userQueryRepository;
	private final RutinRepository rutinRepository;
	@PostMapping()
	public User saveUser(@RequestBody UserDto userDto){
		return userRepository.save(userDto.toEntity());
	}

	@GetMapping()
	public List<User> allUser(){
		return userRepository.getUserListByQueryDsl();
	}

	@GetMapping("/list/{name}")
	public List<GetUserDto> getUsers(@PathVariable("name") String name){
		List<User> findUsers = userQueryRepository.getSameNameUserByQueryDsl(name);
		return findUsers.stream().map(GetUserDto::new).collect(Collectors.toList());
	}

	@GetMapping("/{name}")
	public GetUserDto getUser(
		@PathVariable("name")String name
	){
		User findUser = userRepository.findByName(name).orElseThrow(RuntimeException::new);
		return new GetUserDto(findUser);
	}
}
