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
@Table(name = "witnesses")
public class Witness {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "varchar(500) not null")
    private String statement;

    @Column(nullable = false)
    private Double reliabilityScore;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private String voiceTone;

    @ManyToOne
    @JoinColumn(name = "case_id")
    @JsonIgnore
    private Case witnessCase;

    @OneToMany(mappedBy = "witness", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Question> questions;
}
