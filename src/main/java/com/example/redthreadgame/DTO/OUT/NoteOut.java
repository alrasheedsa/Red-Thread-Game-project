package com.example.redthreadgame.DTO.OUT;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteOut {

    private Integer id;
    private String content;

    // TODO: add after relations are ready
    // private Integer gameSessionId;
    // private String playerName;
}
