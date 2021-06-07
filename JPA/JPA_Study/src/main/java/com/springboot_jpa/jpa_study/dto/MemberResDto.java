package com.springboot_jpa.jpa_study.dto;

import com.springboot_jpa.jpa_study.domain.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MemberResDto {
    private Long id;
    private String name;

    public MemberResDto(Member member){
        this.id = member.getId();
        this.name = member.getName();
    }
}
