package com.security.springbootsecurityjwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.security.springbootsecurityjwt.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
