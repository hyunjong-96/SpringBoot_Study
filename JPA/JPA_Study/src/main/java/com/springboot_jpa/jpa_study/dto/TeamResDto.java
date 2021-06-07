package com.springboot_jpa.jpa_study.dto;

import com.springboot_jpa.jpa_study.domain.Team;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
public class TeamResDto {
    private Long id;
    private String name;
    private List<MemberResDto> memberResDto;

    public TeamResDto(Team team){
        this.id = team.getId();
        this.name = team.getName();
        this.memberResDto = team.getMembers().stream().map(MemberResDto::new).collect(Collectors.toList());
    }
}
