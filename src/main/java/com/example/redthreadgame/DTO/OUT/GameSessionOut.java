package com.example.redthreadgame.DTO.OUT;

import com.example.redthreadgame.Enums.GameSessionStatusType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GameSessionOut {

    private Integer id;
    private GameSessionStatusType status;
    private Boolean isPrivate;
    private Integer playersCount;
    private Integer questionsCount;
    private Integer score;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    private CaseOut sessionCase;
    private Set<HintOut> hints;
    private Set<NoteOut> notes;
    private Set<SolutionProposalOut> solutionProposals;
    private Set<SessionPlayerOut> sessionPlayers;
}
