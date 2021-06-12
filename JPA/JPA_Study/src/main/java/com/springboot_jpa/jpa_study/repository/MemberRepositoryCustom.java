package com.springboot_jpa.jpa_study.repository;

import java.util.List;
import java.util.Optional;

import com.springboot_jpa.jpa_study.domain.Member;
import com.springboot_jpa.jpa_study.domain.Team;

public interface MemberRepositoryCustom {
	List<Member> searchMembers();
	Optional<Member> searchMember(Long memberId);
	List<Member> searchTeamMember(Team team);
}
