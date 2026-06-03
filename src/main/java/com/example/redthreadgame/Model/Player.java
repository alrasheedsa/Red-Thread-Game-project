package com.example.redthreadgame.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(30)", nullable = false)
    private String name;

    @Column(columnDefinition = "varchar(20)", nullable = false, unique = true)
    private String username;

    @Column(columnDefinition = "varchar(50)", nullable = false, unique = true)
    private String email;

    @Column(columnDefinition = "varchar(50)", nullable = false)
    private String password;

    @Column(columnDefinition = "int", nullable = false)
    private Integer age;

    @Column(columnDefinition = "int default 0", insertable = false)
    private Integer score;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    private Set<Invitation> invitations;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Note> notes;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<SolutionProposal> solutionProposals;

}
