package com.security.springbootsecurityjwt.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class User implements UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence_gen")
	@SequenceGenerator(name = "user_sequence_gen", sequenceName = "user_sequence")
	private Long id;

	private String name;

	private String email;

	private String password;

	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> roles = new ArrayList<>();

	@Builder
	public User(Long id, String name, String email, String password){
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {//계정이 갖고있는 권한 목록을 리턴한다.
		return this.roles.stream()
			.map(SimpleGrantedAuthority::new)
			.collect(Collectors.toList());
	}

	@Override
	public String getUsername() {
		return email;
	}//계정의 이름을 리턴한다.

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}//계정이 만료되지 않았는 지 리턴한다. (true: 만료안됨)

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}// 계정이 잠겨있지 않았는 지 리턴한다. (true: 잠기지 않음)

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}// 비밀번호가 만료되지 않았는 지 리턴한다. (true: 만료안됨)

	@Override
	public boolean isEnabled() {
		return true;
	}//계정이 활성화(사용가능)인 지 리턴한다. (true: 활성화)
}
