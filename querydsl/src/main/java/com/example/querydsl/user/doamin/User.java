package com.example.querydsl.user.doamin;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.springframework.data.annotation.CreatedDate;

import com.example.querydsl.common.domain.BaseTimeEntity;
import com.example.querydsl.rutin.domain.Rutin;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class User extends BaseTimeEntity{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private Integer age;

	@OneToMany(mappedBy = "user")
	private List<Rutin> rutins = new ArrayList<>();

	@Builder
	private User(Long id, String name, Integer age){
		this.id = id;
		this.name = name;
		this.age = age;
	}

	public void setRutins(Rutin rutin){
		rutins.add(rutin);
	}
}
