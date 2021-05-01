package hello.hellospring.service;

import hello.hellospring.repository.MemoryTestRepository;

public class TestService {
    private final MemoryTestRepository memoryTestRepository;

    public TestService(MemoryTestRepository memoryTestRepository) {
        this.memoryTestRepository = memoryTestRepository;
    }
}
