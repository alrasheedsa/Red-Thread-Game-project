package com.example.redthreadgame.DTO.OUT;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolutionProposalOut {
    private Integer id;
    private String reason;
    private String status;
    private Integer rejectCount;
    private Integer acceptCount;

    // TODO: add after relations are ready
    // private Integer gameSessionId;
    // private String playerName;
    // private String suspectName;
}
