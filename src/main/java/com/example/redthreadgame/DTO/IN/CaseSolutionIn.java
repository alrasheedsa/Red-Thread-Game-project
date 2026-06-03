package com.example.redthreadgame.DTO.IN;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CaseSolutionIn {
    @NotEmpty(message = "justification cannot be empty")
    private String justification;
}
