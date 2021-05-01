package hello.hellospring.service;

import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemberRepository;
import hello.hellospring.repository.MemoryMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {
    private final MemoryMemberRepository memorymemberRepository;

    @Autowired
    public  MemberService(MemoryMemberRepository memorymemberRepository){
        this.memorymemberRepository = memorymemberRepository;
    }

    //회원가입
    public Long join(Member member){
        //중복회원 검증
        validateDuplicateMember(member); //ctrl+alt+m : extract method 생성

        memorymemberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        memorymemberRepository.findByName(member.getName()) //ctrl+alt+v : 반환되는 타입에 맞게 변수를 만들어줌
                .ifPresent(m -> {   //Optional객체가 감싸고 있는 값이 존재할 경우에만 실행
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
    }

    //전체회원 조회
    public List<Member> findMembers(){
         return memorymemberRepository.findAll();
    }
}
