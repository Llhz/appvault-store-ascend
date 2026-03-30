package com.appvault.dto;

import com.appvault.validation.PasswordMatches;
import lombok.Data;

import javax.validation.constraints.*;

@Data
@PasswordMatches
public class UserRegistrationDto {

    @NotBlank
    @Size(max = 50)
    private String firstName;

    @NotBlank
    @Size(max = 50)
    private String lastName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank
    private String confirmPassword;
}
