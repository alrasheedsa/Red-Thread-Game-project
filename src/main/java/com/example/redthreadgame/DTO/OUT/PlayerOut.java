package com.example.redthreadgame.DTO.OUT;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PlayerOut {

    private Integer id;
    private String name;
    private String username;
    private String email;
    private Integer age;
    private Integer score;

//    private Set<InvitationOut> invitations;
//    private Set<NoteOut> notes;
//    private Set<SolutionProposalOut> solutionProposals;
}
