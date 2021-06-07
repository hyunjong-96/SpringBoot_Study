package com.springboot_jpa.jpa_study.controller;

import com.springboot_jpa.jpa_study.dto.MemberReqDto;
import com.springboot_jpa.jpa_study.dto.TeamReqDto;
import com.springboot_jpa.jpa_study.dto.TeamResDto;
import com.springboot_jpa.jpa_study.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/test")
public class TestController {
    private final TestService testService;

    @PostMapping("/team")
    public Long registerTeam(@RequestBody TeamReqDto teamReqDto){
        return testService.registerTeam(teamReqDto);
    }

    @PostMapping("/member/{id}")
    public Long registerMember(
            @PathVariable("id") Long teamId,
            @RequestBody MemberReqDto memberReqDto
    ){
        return testService.registerMember(teamId,memberReqDto);
    }

    @GetMapping("/team")
    public List<TeamResDto> getTeam(){
        return testService.getTeamInfo();
    }

    @GetMapping("/like/{member}/{team}")
    public void setLike(@PathVariable("member")Long memberId,
                        @PathVariable("team")Long teamId){
        testService.setLike(memberId,teamId);
    }
}

