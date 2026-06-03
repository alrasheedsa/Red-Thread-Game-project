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
public class EvidenceIn {
    @NotEmpty(message = "title cannot be empty")
    private String title;

    @NotEmpty(message = "description cannot be empty")
    private String description;
}
