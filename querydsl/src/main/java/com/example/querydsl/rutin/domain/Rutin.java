package com.example.querydsl.rutin.domain;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.example.querydsl.common.domain.BaseTimeEntity;
import com.example.querydsl.user.doamin.User;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class Rutin extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String part;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	public void setUser(User user){
		if(this.user != null){
			user.getRutins().remove(this);
		}
		this.user = user;
		user.getRutins().add(this);
	}
}
