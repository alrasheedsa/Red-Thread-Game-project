package com.example.redthreadgame.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "suspects")
public class Suspect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer age;

    @ManyToOne
    @JoinColumn(name = "case_id")
    @JsonIgnore
    private Case suspectCase;;

    @OneToMany(mappedBy = "suspect", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<SolutionProposal> solutionProposal;
}