package com.springboot_jpa.jpa_study.repository;

import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot_jpa.jpa_study.domain.Member;
import com.springboot_jpa.jpa_study.domain.QMember;

@Repository
public class MemberRepositorySupport extends QuerydslRepositorySupport {
	private final JPAQueryFactory queryFactory;
	private final QMember qMember = QMember.member;

	public MemberRepositorySupport(JPAQueryFactory jpaQueryFactory){
			super(Member.class);
			this.queryFactory = jpaQueryFactory;
		}

		public List<Member> getMembersByQueryDsl(){
			return queryFactory
				.selectFrom(qMember)
				.fetch();
		}
}
