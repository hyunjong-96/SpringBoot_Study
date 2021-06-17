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
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	@Autowired
	private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
	@Autowired
	private CustomAccessDeniedHandler customAccessDeniedHandler;

	@Override
	protected void configure(HttpSecurity http) throws Exception{
		// http
		// 	.csrf().disable().authorizeRequests()
		// 	.antMatchers("/h2-console/*","/api/user/registration").permitAll()//h2-console로 접근하는 URL허용(antMatchers : 특정 리소스에 대해서 권한설정)
		// 	.anyRequest().authenticated()//토콘을 활용하는 경우 모든 요청에 대해 접근이 가능
		// 	.and().httpBasic()
		//
		// 	.and()
		// 	.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		//
		// 	.and()
		// 	.headers().frameOptions().disable()//X-Frame_Options in Spring Secuirty중지
		//
		// 	.and()
		// 	.formLogin()
		// 	.disable()//폼로그인 비활성
		// 	.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),UsernamePasswordAuthenticationFilter.class)
		// 	//UsernamePasswordAuthenticationFilter전에 커스텀한 Filter적용
		// 	.addFilterBefore(new CustomAuthenticationFilter(authenticationManager()),UsernamePasswordAuthenticationFilter.class);
		http
			.httpBasic().disable()
			.csrf().disable()
			.exceptionHandling()
			.authenticationEntryPoint(customAuthenticationEntryPoint)//인증실패했을경우
			.accessDeniedHandler(customAccessDeniedHandler)//접근권한없는 사용자 접근
			.and()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.headers().frameOptions().disable()//X-Frame_Options in Spring Secuirty중지
			.and()
			.authorizeRequests()
			.antMatchers("/h2-console/*").permitAll()
			.antMatchers("/api/user/login","/api/user/registration").permitAll()
			.anyRequest().authenticated()//그 외의 API는 전부 인증이 필요
			.and()
			.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),UsernamePasswordAuthenticationFilter.class);
	}

	@Override
	protected  void configure(AuthenticationManagerBuilder auth) throws Exception{
		auth.authenticationProvider(authProvider);
	}

	@Bean
	public CustomAuthenticationFilter customAuthenticationFilter() throws Exception{
		CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager());
		return customAuthenticationFilter;
	}

	@Bean
	public PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}
}
