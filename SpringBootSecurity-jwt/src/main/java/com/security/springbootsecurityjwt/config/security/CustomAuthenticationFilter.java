package com.security.springbootsecurityjwt.config.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.springbootsecurityjwt.common.customException.InputNotFoundException;
import com.security.springbootsecurityjwt.domain.User;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	public CustomAuthenticationFilter(final AuthenticationManager authenticationManager){
		super.setAuthenticationManager(authenticationManager);
	}

	//UsernamePasswordAuthenticationToken은 Authentication인터페이스의 구현체.
	//Authentication을 구현한 구현체여야만 AuthenticationManager에서 인증 과정을 수행할수있다.
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
		throws AuthenticationException{
		UsernamePasswordAuthenticationToken authentication;

		try{
			//가로챈 정보를 User엔티티에 맞게 mapping해준다.
			final User user = new ObjectMapper().readValue(request.getInputStream(), User.class);
			//User엔티티로 매핑한 정보에서 사용자의 email과 password를 가져와 인증용 ㅇ객체(UsernamePasswordAuthenticationToken)을 만든다.
			authentication = new UsernamePasswordAuthenticationToken(user.getEmail(),user.getPassword());
		} catch (IOException e) {
			throw new InputNotFoundException("비교할 아이디나 비밀번호가 없습니다");
		}
		setDetails(request, authentication);
		return this.getAuthenticationManager().authenticate(authentication); //AuthenticationManager에게 인증용객체를 인증해달라고 던져준다.
	}
}
