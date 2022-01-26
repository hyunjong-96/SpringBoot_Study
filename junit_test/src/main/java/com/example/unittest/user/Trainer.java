package com.example.unittest.user;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.example.unittest.schedule.Schedule;
import com.example.unittest.schedule.VoucherMatching;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Trainer extends User{

	@OneToMany(cascade = CascadeType.PERSIST,orphanRemoval = true)
	private List<Schedule> scheduleList = new ArrayList<>();

	@OneToOne(fetch = FetchType.LAZY)
	private VoucherMatching voucherMatching;

	@Builder
	public Trainer(String name, Integer age, Role role){
		super(null, name, age, role);
	}

	public void addSchedule(Schedule schedule){
		this.scheduleList.add(schedule);
	}

	public void setVoucherMatching(VoucherMatching voucherMatching){
		this.voucherMatching = voucherMatching;
	}
}
