package com.security.springbootsecurityjwt.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private CustomAuthenticationProvider authProvider;

	@Override
	protected void configure(HttpSecurity http) throws Exception{
		http
			.csrf().disable().authorizeRequests()
			.antMatchers("/h2-console/*").permitAll()//h2-console로 접근하는 URL허용(antMatchers : 특정 리소스에 대해서 권한설정)
			.anyRequest().permitAll()//토콘을 활용하는 경우 모든 요청에 대해 접근이 가능

			.and()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

			.and()
			.headers().frameOptions().disable()//X-Frame_Options in Spring Secuirty중지

			.and()
			.formLogin()
			.disable()//폼로그인 비활성
			//UsernamePasswordAuthenticationFilter전에 커스텀한 Filter적용
			.addFilterBefore(new CustomAuthenticationFilter(authenticationManager()),UsernamePasswordAuthenticationFilter.class);
	}

	@Override
	protected  void configure(AuthenticationManagerBuilder auth) throws Exception{
		auth.authenticationProvider(authProvider);
	}

	@Bean
	public PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}
}
