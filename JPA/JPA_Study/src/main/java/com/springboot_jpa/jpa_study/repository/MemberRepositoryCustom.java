package com.springboot_jpa.jpa_study.repository;

import java.util.List;

import com.springboot_jpa.jpa_study.domain.Member;

public interface MemberRepositoryCustom {
	List<Member> getMembersByQueryDsl();
	Member getMemberByQueryDsl(Long memberId);
}
