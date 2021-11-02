package com.study.springboot_log.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/api/upload")
@RestController
public class OtherController {

	@GetMapping("/all")
	public ResponseEntity<Resource> otherLogTest (
		@RequestParam(value = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate localDate
	) throws IOException {
		String fileName = "application_error.log."+localDate+".gz";
		String filePath = "./logs/all/";

		// ClassLoader classLoader = getClass().getClassLoader();
		// InputStream inputStream = classLoader.getResourceAsStream(fileName);

		Path path = Paths.get(filePath+fileName);
		Resource resource = new InputStreamResource(Files.newInputStream(path));

		log.info("");
		new File("./filetest/"+resource+"");


		return ResponseEntity.ok()
			.contentType(MediaType.parseMediaType("application/octet-stream"))
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+fileName+"\"")
			.body(resource);
	}
}
