package com.example.redthreadgame.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cases")
public class Case {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "varchar(500)")//text
    private String scenario;

    @Column(nullable = false)
    private String difficulty;

    @Column(columnDefinition = "varchar(20) check (status = 'DRAFT' or status = 'PUBLISHED')", nullable = false)
    private String status; // set by default Draft or Publish

//    @ManyToOne
//    @JoinColumn(name = "admin_id")
//    @JsonIgnore
//    private Admin admin;

    @OneToMany(mappedBy = "witnessCase", cascade = CascadeType.ALL)
    private Set<Witness> witnesses;

    @OneToMany(mappedBy = "suspectCase", cascade = CascadeType.ALL)
    private Set<Suspect> suspects;

    @OneToMany(mappedBy = "evidenceCase", cascade = CascadeType.ALL)
    private Set<Evidence> evidences;

    @OneToOne(mappedBy = "solutionCase", cascade = CascadeType.ALL)
    @JsonIgnore
    private CaseSolution caseSolution;

    @OneToMany(mappedBy = "sessionCase", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<GameSession> gameSessions;
}