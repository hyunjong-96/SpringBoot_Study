package com.example.unittest.schedule;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.example.unittest.user.Trainer;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
public class VoucherMatching {
	@Id @GeneratedValue
	private Long id;

	@OneToOne
	private Trainer trainer;

	@OneToMany(mappedBy = "voucherMatching")
	private List<Schedule> scheduleList = new ArrayList<>();

	private String memo;

	@Builder
	public VoucherMatching(String memo){
		this.memo = memo;
	}

	public void setTrainer(Trainer trainer){
		this.trainer = trainer;
		trainer.setVoucherMatching(this);
	}

	public void addSchedule(Schedule schedule){
		this.scheduleList.add(schedule);
	}
}
