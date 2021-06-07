package com.example.test.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "TEAM_SEQ_NUMBER")
    @SequenceGenerator(name = "TEAM_SEQ_NUMBER", sequenceName = "TEAM_SEQUENCE")
    private Long id;
}
