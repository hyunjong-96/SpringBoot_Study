package com.security.springbootsecurityjwt.common.customException;

public class NotFoundUser extends RuntimeException{

	public NotFoundUser(String message) {
		super(message+" 이메일의 사용자는 찾을수 없습니다.");
	}
}
