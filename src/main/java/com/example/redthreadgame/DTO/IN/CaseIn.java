package com.example.redthreadgame.DTO.IN;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CaseIn {


    @NotEmpty(message = "title cannot be empty")
    private String title;

    @NotEmpty(message = "scenario cannot be empty")
    private String scenario;

    @NotEmpty(message = "difficulty cannot be empty")
    private String difficulty;
}
