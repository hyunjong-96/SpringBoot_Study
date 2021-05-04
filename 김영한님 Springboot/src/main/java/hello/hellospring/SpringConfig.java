package hello.hellospring;

import hello.hellospring.repository.JdbcMemberRepository;
import hello.hellospring.repository.MemberRepository;
import hello.hellospring.service.MemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class SpringConfig {

    private DataSource dataSource;

    public SpringConfig(DataSource dataSource){
        this.dataSource = dataSource;
    }

    @Bean
    public MemberService testOfService(){
        return new MemberService(testRepository());
    }

    @Bean
    public MemberRepository testRepository(){
        return new JdbcMemberRepository(dataSource);
        //return new MemoryTestRepository();
    }
}
