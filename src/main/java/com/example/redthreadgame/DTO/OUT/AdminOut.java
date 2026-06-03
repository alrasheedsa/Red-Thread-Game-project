package com.example.redthreadgame.DTO.OUT;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminOut {
    private Integer id;
    private String name;
    private String username;
    private String email;
}