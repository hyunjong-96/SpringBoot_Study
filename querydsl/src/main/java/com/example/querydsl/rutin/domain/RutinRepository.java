package com.example.querydsl.rutin.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RutinRepository extends JpaRepository<Rutin, Long> {
}
