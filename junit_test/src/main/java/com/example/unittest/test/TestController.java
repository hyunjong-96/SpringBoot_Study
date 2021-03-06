package com.example.unittest.test;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/test")
@RestController
public class TestController {

	@PostMapping
	public ResponseEntity<RequestBodyDto> requestBodyTest(
		@RequestBody RequestBodyDto requestBodyDto
	){
		return ResponseEntity.ok().body(requestBodyDto);
	}

	@PostMapping("/modelattribute")
	public ResponseEntity<ModelAttributeDto> modelAttributeTest(
		@ModelAttribute ModelAttributeDto modelAttributeDto
	){
		return ResponseEntity.ok().body(modelAttributeDto);
	}
}
