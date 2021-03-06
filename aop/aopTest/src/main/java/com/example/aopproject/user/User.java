package com.example.aopproject.user;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class User {
	@Id
	@GeneratedValue
	private Long id;

	private String email;

	private String name;

	@Builder
	public User(String email, String name){
		this.email = email;
		this.name = name;
	}
}
