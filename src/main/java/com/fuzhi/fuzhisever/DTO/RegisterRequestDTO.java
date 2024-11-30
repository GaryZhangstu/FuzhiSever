package com.fuzhi.fuzhisever.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;



@Data
public class RegisterRequestDTO {

    @NotNull(message = "Email cannot be null")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotNull(message = "Password cannot be null")
    @NotBlank(message = "Password cannot be blank")
    private String pwd;

    private Integer gender;
    private String phoneNumber;
    private String name;
    private Integer age;


}
