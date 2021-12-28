package com.example.aopproject.common;

import java.time.LocalDateTime;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.example.aopproject.history.History;
import com.example.aopproject.history.HistoryRepository;
import com.example.aopproject.user.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
@Aspect
public class UserHistory {
	private final HistoryRepository historyRepository;

	@Pointcut("execution(* com.example.aopproject.user.UserService.update(*)) && args(user)")
	public void updateUser(User user){};

	@AfterReturning("updateUser(user)")
	public void saveHistory(User user){
		History newHistory = History.builder()
			.userId(user.getId())
			.updateDate(LocalDateTime.now())
			.build();
		historyRepository.save(newHistory);
	}
}
