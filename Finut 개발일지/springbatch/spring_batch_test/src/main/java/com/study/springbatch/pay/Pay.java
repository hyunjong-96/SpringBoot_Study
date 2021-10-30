package com.study.springbatch.pay;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
@Entity
public class Pay {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long amount;
	private String txName;
	private LocalDateTime txDateTime;
	private Boolean success;

	@Builder
	public Pay(Long id, Long amount, String txName, String txDateTime, boolean success){
		this.id = id;
		this.amount = amount;
		this.txName = txName;
		this.txDateTime = LocalDateTime.parse(txDateTime, FORMATTER);
		this.success = success;
	}

	public void success() {
		this.success = true;
	}
}
