package com.study.springboot_log.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/api/other")
@RestController
public class OtherController {

	@GetMapping
	public void otherLogTest(){ log.info("other컨트롤러에서 로그 발생");}
}
