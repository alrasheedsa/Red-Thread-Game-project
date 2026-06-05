package com.example.redthreadgame.DTO.OUT;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HintOut {
    private Integer id;
    private String content;
    private Integer deductedPoints;
    private PlayerOut player;
}
