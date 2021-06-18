package com.security.springbootsecurityjwt.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.security.springbootsecurityjwt.common.customException.NotFoundUser;
import com.security.springbootsecurityjwt.config.security.JwtTokenProvider;
import com.security.springbootsecurityjwt.domain.User;
import com.security.springbootsecurityjwt.dto.GetUserInfoResDto;
import com.security.springbootsecurityjwt.dto.LoginReqDto;
import com.security.springbootsecurityjwt.dto.TokensResDto;
import com.security.springbootsecurityjwt.dto.ReIssueReqDto;
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

	public TokensResDto login(LoginReqDto loginReqDto) {
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!");
		User user = userRepository.findByEmail(loginReqDto.getEmail())
			.orElseThrow(()-> new NotFoundUser("가입되지 않은 이메일입니다"));
		if(!passwordEncoder.matches(loginReqDto.getPassword(), user.getPassword())){
			throw new IllegalStateException("잘못된 비밀번호");
		}
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@");
		String accessToken = jwtTokenProvider.createToken(user.getEmail());
		String refreshToken = jwtTokenProvider.createRefreshToken();
		return new TokensResDto(accessToken,refreshToken);
	}

	public GetUserInfoResDto getUserInfo(UserDetails userDetails) {
		System.out.print(userDetails.getUsername());
		User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(()->new NotFoundUser(userDetails.getUsername()+"id를 가진 사용자를 찾을수 없습니다."));
		return new GetUserInfoResDto(user);
	}

	public TokensResDto getNewToken(ReIssueReqDto reIssueReqDto) {
		//리프레시 토큰 유효성확인
		//repository에서 refreshToken값 비교
		//새로운 토큰생성
		String newAccessToken = jwtTokenProvider.createToken(reIssueReqDto.getAccessToken());
		String newRefreshToken = jwtTokenProvider.createRefreshToken();
		return new TokensResDto(newAccessToken,newRefreshToken);
	}
}
