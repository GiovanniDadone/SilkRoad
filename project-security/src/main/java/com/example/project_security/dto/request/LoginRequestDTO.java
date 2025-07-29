package com.example.project_security.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequestDTO {

    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "Il formato dell'email non è valido")
    private String email;

    @NotBlank(message = "La password è obbligatoria")
    private String password;
}