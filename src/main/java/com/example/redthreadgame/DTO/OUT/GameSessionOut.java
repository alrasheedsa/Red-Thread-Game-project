package com.example.redthreadgame.DTO.OUT;


import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GameSessionOut {

    private Integer id;
    private String status;
    private Boolean isPrivate;
    private Integer playersCount;
    private Integer questionsCount;
    private Integer score;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

//    private Set<HintOut> hints;
//    private Set<NoteOut> notes;
//    private Set<SolutionProposalOut> solutionProposals;
}
