package com.example.redthreadgame.DTO.OUT;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InvitationOut {

    private Integer id;
    private String status;

//    private GameSessionOut gameSession;
//    private Set<PlayerOut> players;
}
