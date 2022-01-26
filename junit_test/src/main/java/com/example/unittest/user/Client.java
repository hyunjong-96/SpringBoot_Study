package com.example.unittest.user;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.example.unittest.schedule.Schedule;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Client extends User{

	@OneToMany(cascade = CascadeType.REMOVE)
	private List<Schedule> scheduleList = new ArrayList<>();

	@Builder
	public Client(String name, Integer age, Role role){
		super(null, name, age, role);
	}

	public void addSchedule(Schedule schedule){
		this.scheduleList.add(schedule);
	}
}
