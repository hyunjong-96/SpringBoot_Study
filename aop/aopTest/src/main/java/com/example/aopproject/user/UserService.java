package com.example.aopproject.user;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.aopproject.common.LogExecutionTime;
import com.example.aopproject.common.SuperPerformance;

import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class UserService extends SuperPerformance<User> {
	private final UserRepository userRepository;

	@Override
	public List<User> findAll() {
		return userRepository.findAll();
	}

	@Override
	public void update(User user){
		userRepository.save(user);
	}

	@LogExecutionTime
	public void createUser(NewUserDto newUserDto) {
		User newUser = User.builder()
			.email(newUserDto.getEmail())
			.name(newUserDto.getName())
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

	public List<User> getUserList(){
		return userRepository.findAll();
	}

}
