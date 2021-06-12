package com.springboot_jpa.jpa_study.repository;

import com.springboot_jpa.jpa_study.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long>,MemberRepositoryCustom, QuerydslPredicateExecutor<Member> {
}
