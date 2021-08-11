package com.example.querydsl.user.doamin;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class UserQueryRepository {
	private final JPAQueryFactory jpaQueryFactory;
	private final QUser user = QUser.user;
	public List<User> getSameNameUserByQueryDsl(String name){
		return jpaQueryFactory
			.selectFrom(user)
			.where(user.name.eq(name))
			.fetch();
	}
}
