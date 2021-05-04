package hello.hellospring.service;

import hello.hellospring.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestService {
    private final MemberRepository memberRepository;

    @Autowired
    public TestService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
}
