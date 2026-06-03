package com.example.redthreadgame.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "case_solutions")
public class CaseSolution {

    @Id
    private Integer id;

    @Column(columnDefinition = "varchar(500) not null")
    private String justification;

    @OneToOne
    @MapsId
    @JoinColumn(name = "case_id")
    private Case solutionCase;

}