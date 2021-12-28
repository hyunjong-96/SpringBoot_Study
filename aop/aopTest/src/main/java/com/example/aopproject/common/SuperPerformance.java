package com.example.aopproject.common;

import java.util.List;

public abstract class SuperPerformance<T> {
	private long before(){
		return System.currentTimeMillis();
	}

	private void after(long start){
		long end = System.currentTimeMillis();
		System.out.println("수행 시간 : "+(end - start));
	}

	public List<T> getDataAll(){
		long start = before();
		List<T> dataList = findAll();
		after(start);

		return dataList;
	}

	public abstract List<T> findAll();
}
