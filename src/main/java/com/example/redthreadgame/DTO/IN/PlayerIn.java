package com.example.redthreadgame.DTO.IN;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PlayerIn {

    @NotEmpty(message = "Name is required")
    @Size(max = 30, message = "Name must not exceed 30 characters")
    private String name;

    @NotEmpty(message = "Username is required")
    @Size(max = 20, message = "Username must not exceed 20 characters")
    private String username;

    @NotEmpty(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 50, message = "Email must not exceed 50 characters")
    private String email;

    @NotEmpty(message = "Phone Number is required")
    @Size(max = 10, message = "Phone Number must not exceed 10 characters")
    private String phoneNumber;

    @NotEmpty(message = "Password is required")
    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
    private String password;

    @NotNull(message = "Age is required")
    @Min(value = 1, message = "Age must be at least 1")
    private Integer age;
}
