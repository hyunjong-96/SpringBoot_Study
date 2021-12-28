package com.example.aopproject.user;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class UserService {
	private final UserRepository userRepository;

	@LogExecutionTime
	public void createUser(String name) {
		User newUser = User.builder()
			.name(name)
			.build();

		userRepository.save(newUser);
	}

	@LogExecutionTime
	public String allUser(){
		List<User> userList = userRepository.findAll();
		List<String> nameList = userList.stream()
			.map(User::getName)
			.collect(Collectors.toList());

		return nameList.toString();
	}
}
