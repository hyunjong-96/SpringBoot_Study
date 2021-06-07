package com.springboot_jpa.jpa_study.dto;

import com.springboot_jpa.jpa_study.domain.Member;
import com.springboot_jpa.jpa_study.domain.Team;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MemberReqDto {
    private String name;

    public Member toEntity(Team team){
        Member newMember = Member.builder()
                .name(name)
                .build();
        newMember.setTeam(team);
        return newMember;
    }
}
