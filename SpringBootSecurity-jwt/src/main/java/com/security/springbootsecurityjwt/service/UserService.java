package com.security.springbootsecurityjwt.service;

import org.springframework.stereotype.Service;

import com.security.springbootsecurityjwt.dto.LoginReqDto;
import com.security.springbootsecurityjwt.dto.RegistrationReqDto;
import com.security.springbootsecurityjwt.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
	private final UserRepository userRepository;

	public void registration(RegistrationReqDto registrationReqDto) {
		userRepository.save(registrationReqDto.toEntity());
	}

	public void login(LoginReqDto loginReqDto) {

	}
}
