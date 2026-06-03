package com.example.redthreadgame.DTO.IN;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolutionProposalIn {

    @NotEmpty(message = "Reason must not be empty")
    private String reason;
}
