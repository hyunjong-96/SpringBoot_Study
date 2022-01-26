package com.example.unittest.schedule;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.example.unittest.user.Client;
import com.example.unittest.user.Trainer;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Schedule {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalDateTime startDateTime;

	private LocalDateTime endDateTime;

	private String title;

	@ManyToOne
	@JoinColumn(name = "voucherMatching_id")
	private VoucherMatching voucherMatching;

	@ManyToOne
	@JoinColumn(name = "trainer_id")
	private Trainer trainer;

	@ManyToOne
	@JoinColumn(name = "client_id")
	private Client client;

	@Builder
	public Schedule(
		LocalDateTime startDateTime, LocalDateTime endDateTime, String title){
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		this.title = title;
	}

	public void setTrainer(Trainer trainer){
		this.trainer = trainer;
		trainer.addSchedule(this);
	}

	public void setClient(Client client){
		this.client = client;
		client.addSchedule(this);
	}

	public void setVoucherMatching(VoucherMatching voucherMatching){
		this.voucherMatching = voucherMatching;
		voucherMatching.addSchedule(this);
	}
}
