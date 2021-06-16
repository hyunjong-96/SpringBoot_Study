package com.security.springbootsecurityjwt.config.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.web.filter.GenericFilterBean;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
	private final JwtTokenProvider jwtTokenProvider;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
		IOException,
		ServletException {
		System.out.println("33333333333333333333333");
		String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);
		if(token != null && jwtTokenProvider.validateToken(token)){	//token검증
			Authentication auth = jwtTokenProvider.getAuthentication(token); //토큰이 유효하면 토큰으로부터 유저 정보를 받아옴.
			//SecurityContextHolder.getContext().setAuthentication(auth); //SecurityContext에 Authentication객체를 저장
		}
		chain.doFilter(request, response);
	}
}
