package com.springboot_jpa.jpa_study.repository;

import com.springboot_jpa.jpa_study.config.TestConfig;
import com.springboot_jpa.jpa_study.domain.Member;
import com.springboot_jpa.jpa_study.domain.Team;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import(TestConfig.class)
// @RunWith(SpringRunner.class)
// @SpringBootTest
public class RepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private MemberRepositoryImpl memberRepositoryImpl;

    @DisplayName("다대일_연결_테스트")
    @Test
    public void ManyToOneTest(){
        //given
        Team team = Team.builder().name("SOPT").build();
        Member member = Member.builder().name("현종").build();
        member.setTeam(team);

        //when
        teamRepository.save(team);
        memberRepository.save(member);
        Team findTeam = teamRepository.findById(team.getId()).orElseThrow(RuntimeException::new);
        Member findMember = memberRepository.findById(member.getId()).orElseThrow(RuntimeException::new);

        //then
        assertThat(findMember.getTeam().getId()).isEqualTo(team.getId());
        assertThat(findTeam.getMembers().size()).isEqualTo(1);
    }

    @DisplayName("QueryDsl_Test")
    @Test
    public void QueryDslTest(){
        //given
        Member member = Member.builder().name("현종").build();

        memberRepository.save(member);

        //when
        List<Member> members = memberRepository.getMembersByQueryDsl();
        // List<Member> members = memberRepository.findAll();

        //then
        assertThat(members.size()).isEqualTo(1);
        assertThat(members.get(0).getName()).isEqualTo("현종");
    }
}
