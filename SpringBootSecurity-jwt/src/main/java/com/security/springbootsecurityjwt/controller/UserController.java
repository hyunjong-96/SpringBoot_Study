package com.security.springbootsecurityjwt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.security.springbootsecurityjwt.dto.GetUserInfoResDto;
import com.security.springbootsecurityjwt.dto.LoginReqDto;
import com.security.springbootsecurityjwt.dto.TokensResDto;
import com.security.springbootsecurityjwt.dto.ReIssueReqDto;
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
	public ResponseEntity<TokensResDto> login(@RequestBody LoginReqDto loginReqDto){
		TokensResDto tokensResDto = userService.login(loginReqDto);
		return ResponseEntity.ok().body(tokensResDto);	//accessToken과 refreshToken을 함꼐 보내줌.
	}

	@GetMapping()
	public ResponseEntity<GetUserInfoResDto> getUserInfo(@AuthenticationPrincipal UserDetails userDetails){//@AutjemtocationPrincipal을 불러올떄는 인정된 객체에 넣어준 Principal의 타입에 맞기 가져와준다
		GetUserInfoResDto getUserInfoResDto = userService.getUserInfo(userDetails);
		return ResponseEntity.ok().body(getUserInfoResDto);
	}

	@PostMapping("/reissue")
	public ResponseEntity<TokensResDto> reissueToken(@RequestBody ReIssueReqDto reIssueReqDto){
		TokensResDto tokensResDto = userService.getNewToken(reIssueReqDto);
		return ResponseEntity.ok().body(tokensResDto);
	}
}
