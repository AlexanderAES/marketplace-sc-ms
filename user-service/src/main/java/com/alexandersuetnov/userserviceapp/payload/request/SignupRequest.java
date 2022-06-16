package com.alexandersuetnov.userserviceapp.payload.request;

import com.alexandersuetnov.userserviceapp.annotations.PasswordMatches;
import com.alexandersuetnov.userserviceapp.annotations.ValidEmail;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@PasswordMatches
public class SignupRequest {
    @Email(message = "It should have email format")
    @NotBlank(message = "User email is required")
    @ValidEmail
    private String email;

    @NotEmpty(message = "Please enter your username")
    private String username;

    @NotEmpty(message = "Please enter your name")
    private String name;

    @NotEmpty(message = "Please enter your phone number")
    private String phoneNumber;

    @NotEmpty(message = "Password is required")
    @Size(min = 6)
    private String password;
    private  String confirmPassword;
}
