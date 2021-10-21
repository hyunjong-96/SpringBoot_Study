package com.study.springboot_log.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/api/log")
@RestController
public class LogController {

	@GetMapping()
	public void logTest(){
		log.info("로그 발생");
	}

	@GetMapping("/warn")
	public void warnTest(){log.error("에러발생");}
}
