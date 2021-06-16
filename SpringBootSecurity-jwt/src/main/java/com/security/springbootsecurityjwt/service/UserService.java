package com.security.springbootsecurityjwt.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.security.springbootsecurityjwt.common.customException.NotFoundUser;
import com.security.springbootsecurityjwt.config.security.JwtTokenProvider;
import com.security.springbootsecurityjwt.domain.User;
import com.security.springbootsecurityjwt.dto.LoginReqDto;
import com.security.springbootsecurityjwt.dto.RegistrationReqDto;
import com.security.springbootsecurityjwt.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
	private final UserRepository userRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final PasswordEncoder passwordEncoder;

	public void registration(RegistrationReqDto registrationReqDto) {
		userRepository.save(registrationReqDto.toEntity());
	}

	public String login(LoginReqDto loginReqDto) {
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!");
		User user = userRepository.findByEmail(loginReqDto.getEmail())
			.orElseThrow(()-> new NotFoundUser("가입되지 않은 이메일입니다"));
		if(!passwordEncoder.matches(loginReqDto.getPassword(), user.getPassword())){
			throw new IllegalStateException("잘못된 비밀번호");
		}
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@");
		return jwtTokenProvider.createToken(user.getEmail());
	}
}
