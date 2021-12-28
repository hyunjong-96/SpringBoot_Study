package com.example.aopproject.user;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class User {
	@Id
	@GeneratedValue
	private Long id;

	private String name;

	@Builder
	public User(String name){
		this.name = name;
	}
}
