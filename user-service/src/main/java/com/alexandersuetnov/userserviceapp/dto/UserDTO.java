package com.alexandersuetnov.userserviceapp.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;


@Data
public class UserDTO {
    private Long id;
    @NotEmpty
    private String username;
    private String name;
    private String phoneNumber;
    private String email;
}
