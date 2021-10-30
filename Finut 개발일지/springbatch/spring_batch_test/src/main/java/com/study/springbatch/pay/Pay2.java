package com.study.springbatch.pay;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Entity
public class Pay2 {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long amount;
	private LocalDateTime txDateTime;
	private Boolean success;

	public Pay2(Long amount, LocalDateTime txDateTime, Boolean success){
		this.amount = amount;
		this.txDateTime = txDateTime;
		this.success = success;
	}
}
