package hello.hellospring;

import hello.hellospring.repository.MemoryTestRepository;
import hello.hellospring.service.TestService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {
    @Bean
    public TestService testService(){
        return new TestService(testRepository());
    }

    @Bean
    public MemoryTestRepository testRepository(){
        return new MemoryTestRepository();
    }
}
