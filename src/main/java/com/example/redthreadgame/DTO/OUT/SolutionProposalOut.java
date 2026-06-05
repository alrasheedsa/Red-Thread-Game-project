package com.example.redthreadgame.DTO.OUT;

import com.example.redthreadgame.Enums.SolutionProposalStatusType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolutionProposalOut {
    private Integer id;
    private String reason;
    private SolutionProposalStatusType status;
    private Integer rejectCount;
    private Integer acceptCount;

     private PlayerOut player;
     private SuspectOut suspect;
}
