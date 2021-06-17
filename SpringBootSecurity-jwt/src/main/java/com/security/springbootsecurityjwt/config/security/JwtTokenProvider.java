package com.security.springbootsecurityjwt.config.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.security.springbootsecurityjwt.common.customException.NotFoundUser;
import com.security.springbootsecurityjwt.common.customException.utils.JwtUtil;
import com.security.springbootsecurityjwt.service.CustomDetailsService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {
	private String secretKey = "apple";
	private final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 1;//30분
	private final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 7;//7일

	@Autowired
	private CustomDetailsService customDetailsService;

	@PostConstruct
	protected void init(){
		secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
	}

	//JWT토큰 생성
	public String createToken(String userPk){
		Claims claims = Jwts.claims().setSubject(userPk);
		Date now = new Date();
		return Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME))
			.signWith(SignatureAlgorithm.HS256,secretKey)
			.compact();
	}

	public Authentication getAuthentication(String username){
		System.out.println("44444444444444444444444");
		UserDetails userDetails = customDetailsService.loadUserByUsername(username);
		return new UsernamePasswordAuthenticationToken(userDetails,"",userDetails.getAuthorities());
	}

	//토큰에서 회원 정보 추출
	public String getUserPk(String token){
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
	}

	//Request의 Header에서 token을 가져옴, "X-AUTH-TOKEN"
	public String resolveToken(HttpServletRequest request){
		return request.getHeader("X-AUTH-TOKEN");
	}

	public String resolveRefreshToken(HttpServletRequest request){
		return request.getHeader("X-AUTH-REFRESHTOKEN");
	}

	//토큰의 유효성 + 만료일자 확인
	public boolean validateToken(String jwtToken){
		try{
			Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
			return !claims.getBody().getExpiration().before(new Date());
		}catch (Exception e){
			System.out.println("kkkkkkkkkkkkkkk");
			return false;
		}
	}
}
