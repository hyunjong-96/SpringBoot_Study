package com.spring_study.group.spring.testproject.Controller;

import com.spring_study.group.spring.testproject.Service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@RestController
public class TestController {
    private  final TestService testService;

    @Autowired
    public TestController(TestService testService){
        this.testService = testService;
    }

    @PostConstruct
    public void initTest(){
        System.out.println("초기화 콜백");
    }

    @PreDestroy
    public void destoryTest(){
        System.out.println("소멸 전 콜백");
    }
}
