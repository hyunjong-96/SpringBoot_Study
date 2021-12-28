package com.example.aopproject.common;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class Performance {

	@Around("execution(* com.example.aopproject.board.BoardService.findAll(..))")
	public Object calculatePerformanceTime(ProceedingJoinPoint joinPoint){
		Object result = null;
		try{
			long start = System.currentTimeMillis();
			result = joinPoint.proceed();
			long end = System.currentTimeMillis();

			System.out.println("수행시간 : "+(end-start));
		}catch(Throwable throwable){
			System.out.println("exception!");
		}
		return result;
	}
}
