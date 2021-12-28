package com.example.aopproject.board;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.aopproject.common.SuperPerformance;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BoardService extends SuperPerformance<Board> {
	private final BoardRepository boardRepository;

	@Override
	public List<Board> findAll() {
		return boardRepository.findAll();
	}
}
