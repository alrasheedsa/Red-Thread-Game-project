package com.example.redthreadgame.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(nullable = false, columnDefinition = "TEXT")
    private String scenario;

    @Column(nullable = false)
    private String difficulty;

    @Column(columnDefinition = "varchar(20) check (status = 'DRAFT' or status = 'PUBLISHED')", nullable = false)
    private String status; // set by default Draft or Publish

//    @ManyToOne
//    @JoinColumn(name = "admin_id")
//    @JsonIgnore
//    private Admin admin;

//    @OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL)
//    private List<Witness> witnesses;
//
//    @OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL)
//    private List<Suspect> suspects;
//
//    @OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL)
//    private List<Evidence> evidences;

    // OneToOne
//    @OneToOne(mappedBy = "caseEntity", cascade = CascadeType.ALL)
//    private CaseSolution caseSolution;

    // @OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL)
    //private List<GameSession> gameSessions;
}