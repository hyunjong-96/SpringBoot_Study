package com.example.querydsl.user.doamin;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class UserRepositoryImpl implements UserRepositoryCustom{
	private final JPAQueryFactory jpaQueryFactory;

	private final QUser user = QUser.user;

	@Override
	public List<User> getUserListByQueryDsl() {
		return jpaQueryFactory
			.selectFrom(user)
			.fetch();
	}

	@Override
	public List<User> getSameNameUserListByQueryDsl(String name) {
		return jpaQueryFactory
			.selectFrom(user)
			.where(user.name.eq(name))
			.fetch();
	}
}
