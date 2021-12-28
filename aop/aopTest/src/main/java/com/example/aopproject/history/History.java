package com.example.aopproject.history;

import java.time.LocalDateTime;

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
public class History {
	@Id
	@GeneratedValue
	private Long id;

	private long userId;

	private LocalDateTime updateDate;

	@Builder
	public History(long userId, LocalDateTime updateDate){
		this.userId = userId;
		this.updateDate = updateDate;
	}
}
