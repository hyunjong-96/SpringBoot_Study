package com.example.aopproject.board;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/board")
@RestController
public class BoardController {
	private final BoardService boardService;

	@GetMapping("/list")
	public List<Board> getBoardList(){
		return boardService.getDataAll();
	}
}
