package com.security.springbootsecurityjwt.common.customException.utils;

import java.util.Base64;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {
	private static String secretKey = "apple";
	private static final long TOKEN_VALIDATION_SECOND = 1000L * 60 * 60; //1시간
	private static final long  REFRESH_TOEN_VALIDATION_SECOND = 1000L * 60 * 24 *2;

	final static private String ACCESS_TOKEN_NAME = "accessTokne";
	final static private String REFRESH_TOKEN_NAME = "refreshToken";

	@PostConstruct
	protected void init(){secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());}

	//JWT토큰 생성
	public String createToken(String userPk){
		Claims claims = Jwts.claims().setSubject(userPk);
		Date now = new Date();
		return Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + TOKEN_VALIDATION_SECOND))
			.signWith(SignatureAlgorithm.HS256,secretKey)
			.compact();
	}

	//토큰에서 회원 정보 추출
	public String getUserPk(String token){
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
	}

	//Request의 Header에서 token을 가져옴, "X-AUTH-TOKEN"
	public String resolveToken(HttpServletRequest request){
		return request.getHeader("X-AUTH-TOKEN");
	}

	//토큰의 유효성 + 만료일자 확인
	public boolean validateToken(String jwtToken){
		try{
			Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
			return !claims.getBody().getExpiration().before(new Date());
		}catch (Exception e){
			return false;
		}
	}
}
