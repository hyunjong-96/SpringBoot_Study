package hello.hellospring.controller;

import hello.hellospring.service.TestService;
import org.springframework.stereotype.Controller;

@Controller
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }
}
