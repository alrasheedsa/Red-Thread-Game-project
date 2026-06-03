package com.example.redthreadgame.DTO.IN;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WitnessIn {
    @NotEmpty(message = "name cannot be empty")
    private String name;

    @NotEmpty(message = "statement cannot be empty")
    private String statement;
    @NotNull(message = "reliability score cannot be null")

    @Min(value = 0 , message = "reliability score minimum is 0")
    @Max(value = 100 , message = "reliability score maximum is 100")
    private Double reliabilityScore;
}
