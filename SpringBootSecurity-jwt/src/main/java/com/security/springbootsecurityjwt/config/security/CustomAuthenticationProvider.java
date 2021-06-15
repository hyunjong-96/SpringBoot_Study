package com.security.springbootsecurityjwt.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.security.springbootsecurityjwt.common.customException.NotFoundUser;
import com.security.springbootsecurityjwt.service.CustomDetailsService;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private CustomDetailsService customDetailsService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		final String email = authentication.getPrincipal().toString();	//인증용 객체에서 아이디
		final String password = authentication.getCredentials().toString(); //인증용 객체에서 비밀번호

		UserDetails loadedUser = customDetailsService.loadUserByUsername(email);	//userDetailsService인터페이스를 구현한 customUserDetailsService로 부터 아이디를 통해 DB에서 정보를 가져옴
		if(loadedUser == null){
			throw new NotFoundUser("(AuthenticationProvider)_사용자가 없다고합니다.");
		}
		if(passwordEncoder.matches(password,loadedUser.getPassword())){//BCryptPasswordEncoder를 통해서 DB에서가져온 인코딩된 비밀번호랑 비교.
			throw new NotFoundUser("(AuthenticationProvider)_비밀번호가 다르다고합니다.");
		}
		UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(loadedUser.getUsername(),loadedUser.getPassword());
		result.setDetails(authentication.getDetails());
		return result;	//인증된 인증용 객체
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
