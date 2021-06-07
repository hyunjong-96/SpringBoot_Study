package com.springboot_jpa.jpa_study.repository;

import com.springboot_jpa.jpa_study.domain.TeamLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<TeamLike, Long> {
}
