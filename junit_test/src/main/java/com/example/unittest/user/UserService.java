package com.example.unittest.user;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.example.unittest.user.dto.UserDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
	private final UserRepository userRepository;

	@Transactional
	public void repetitionUserSave(UserDto userDto) {
		log.info("repetitionUserSave : {}",TransactionSynchronizationManager.getCurrentTransactionName());
		this.isOtherTransaction(userDto);
		throw new RuntimeException();
	}

	@Transactional
	public void isOtherTransaction(UserDto userDto){
		log.info("isOtherTransaction : {}",TransactionSynchronizationManager.getCurrentTransactionName());
		for (int i = 0; i < 5; i++) {
			userRepository.save(userDto.toEntity());
		}
	}

	public UserDto getUserInfo(Long userId) {
		User findUser = userRepository.findById(userId)
			.orElseThrow(()->new NoSuchElementException(userId+"를 찾을수 없습니다"));
		return UserDto.builder().name(findUser.getName()).age(findUser.getAge()).role(findUser.getRole()).build();
	}

	@Transactional
	public UserDto saveUser(UserDto userDto) {
		User user = userRepository.save(userDto.toEntity());
		return new UserDto(user.getName(), user.getAge(), user.getRole());
	}
}
