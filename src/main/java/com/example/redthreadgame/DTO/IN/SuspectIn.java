package com.example.redthreadgame.DTO.IN;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuspectIn {
    @NotEmpty(message = "name cannot be empty")
    private String name;
    @NotNull(message = "age cannot be null")
    @Min(value = 1, message = "age must be positive")
    private Integer age;
    @NotEmpty(message = "gender cannot be empty")
    private String gender;
    @NotEmpty(message = "voice tone cannot be empty")
    private String voiceTone;
    private String reason;

}
