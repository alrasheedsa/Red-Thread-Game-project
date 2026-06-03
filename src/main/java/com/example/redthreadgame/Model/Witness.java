package com.example.redthreadgame.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(nullable = false, columnDefinition = "TEXT")
    private String statement;

    @Column(nullable = false)
    private Double reliabilityScore;

//    @ManyToOne
//    @JoinColumn(name = "case_id")
//    @JsonIgnore
//    private Case caseEntity;
}
