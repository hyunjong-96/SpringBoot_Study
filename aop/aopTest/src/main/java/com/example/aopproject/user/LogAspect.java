package com.example.aopproject.user;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
@Aspect
public class LogAspect {
	Logger logger = LoggerFactory.getLogger(LogAspect.class);

	// @Around("@annotation(LogExecutionTime)")
	// public Object logExecutionTIme(ProceedingJoinPoint joinPoint) throws Throwable{
	// 	StopWatch stopWatch = new StopWatch();
	// 	stopWatch.start();
	//
	// 	Object proceed = joinPoint.proceed();
	//
	// 	stopWatch.stop();
	// 	logger.info(stopWatch.prettyPrint());
	//
	// 	return proceed;
	// }

	@Pointcut("execution(* com.example.aopproject.user.UserService.createUser(..))")
	public void createUser(){}

	@Pointcut("execution(* com.example.aopproject.user.UserService.allUser(..))")
	public void allUser(){}

	@Around("createUser() || allUser()")
	public Object logExecutionTimeByPath(ProceedingJoinPoint joinPoint) throws Throwable{
		Object result = null;
		try{
			StopWatch stopWatch = new StopWatch();
			stopWatch.start();

			result = joinPoint.proceed();

			stopWatch.stop();

			logger.info(stopWatch.prettyPrint());
		}catch(Throwable throwable){
			System.out.println("exception!!!");
		}

		return result;
	}
}
