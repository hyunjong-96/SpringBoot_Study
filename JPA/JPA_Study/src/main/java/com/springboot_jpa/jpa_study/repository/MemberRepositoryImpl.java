package com.springboot_jpa.jpa_study.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot_jpa.jpa_study.domain.Member;
import com.springboot_jpa.jpa_study.domain.Team;

public class MemberRepositoryImpl implements MemberRepositoryCustom{
	private final JPAQueryFactory queryFactory;

	public MemberRepositoryImpl(EntityManager em){
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public List<Member> searchMembers() {
		return null;
	}

	@Override
	public Optional<Member> searchMember(Long memberId) {
		return Optional.empty();
	}

	@Override
	public List<Member> searchTeamMember(Team team) {
		return null;
	}
}
