package com.example.aopproject.board;

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
public class Board {
	@Id
	@GeneratedValue
	private Long id;

	private String title;

	private String content;

	@Builder
	public Board(String title, String content){
		this.title = title;
		this.content = content;
	}
}
