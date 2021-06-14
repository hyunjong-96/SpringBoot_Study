package com.springboot_jpa.jpa_study.repository;

import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot_jpa.jpa_study.domain.Member;
import static com.springboot_jpa.jpa_study.domain.QMember.member;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class MemberRepositoryImpl implements MemberRepositoryCustom{

	private final JPAQueryFactory queryFactory;

		public List<Member> getMembersByQueryDsl(){
			return queryFactory
				.selectFrom(member)
				.fetch();
		}

		public Member getMemberByQueryDsl(Long memberId){
			return queryFactory
				.selectFrom(member)
				.where(
					eqId(memberId)
				)
				.fetchOne();
		}

		private BooleanExpression eqId(Long memberId){
			return memberId != null ? member.id.eq(memberId) : null;
		}
}
