package com.springboot_jpa.jpa_study.service;

import com.springboot_jpa.jpa_study.domain.TeamLike;
import com.springboot_jpa.jpa_study.domain.Member;
import com.springboot_jpa.jpa_study.domain.Team;
import com.springboot_jpa.jpa_study.dto.MemberReqDto;
import com.springboot_jpa.jpa_study.dto.TeamReqDto;
import com.springboot_jpa.jpa_study.dto.TeamResDto;
import com.springboot_jpa.jpa_study.repository.LikeRepository;
import com.springboot_jpa.jpa_study.repository.MemberRepository;
import com.springboot_jpa.jpa_study.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TestService {
    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;
    private final LikeRepository likeRepository;

    public Long registerTeam(TeamReqDto teamReqDto) {
        return teamRepository.save(teamReqDto.toEntity()).getId();
    }

    public Long registerMember(Long teamId, MemberReqDto memberReqDto) {
        Team findTeam = teamRepository.findById(teamId).orElseThrow(RuntimeException::new);
        return memberRepository.save(memberReqDto.toEntity(findTeam)).getId();
    }

    public List<TeamResDto> getTeamInfo() {
        Member findMember = memberRepository.findById(1L).orElseThrow(RuntimeException::new);
        findMember.getTeam();
        return teamRepository.findAll().stream().map(TeamResDto::new).collect(Collectors.toList());
    }

    public void setLike(Long memberId,Long teamId) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(RuntimeException::new);
        Team findTeam = teamRepository.findById(teamId).orElseThrow(RuntimeException::new);

        TeamLike newTeamLike = TeamLike.builder().team(findTeam).member(findMember).build();
        likeRepository.save(newTeamLike);
    }
}
