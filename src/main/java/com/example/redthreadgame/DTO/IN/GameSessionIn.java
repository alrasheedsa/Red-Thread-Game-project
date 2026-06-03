package com.example.redthreadgame.DTO.IN;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GameSessionIn {

    @NotNull(message = "isPrivate is required")
    private Boolean isPrivate;

    @NotNull(message = "Players count is required")
    @Min(value = 1, message = "Players Count must be at least 1")
    @Max(value = 6, message = "Players Count must not exceed 6")
    private Integer playersCount;
}
