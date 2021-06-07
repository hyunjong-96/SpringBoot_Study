package com.springboot_jpa.jpa_study.repository;

import com.springboot_jpa.jpa_study.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team,Long> {
}
