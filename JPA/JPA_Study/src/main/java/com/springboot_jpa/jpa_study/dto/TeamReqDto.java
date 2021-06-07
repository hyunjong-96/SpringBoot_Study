package com.springboot_jpa.jpa_study.dto;

import com.springboot_jpa.jpa_study.domain.Team;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TeamReqDto {
    private String name;

    public Team toEntity(){
        return Team.builder()
                .name(this.name)
                .build();
    }
}
