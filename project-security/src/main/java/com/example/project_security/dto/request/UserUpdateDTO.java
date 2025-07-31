package com.example.project_security.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per l'aggiornamento dei dati utente
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDTO {

    @Size(min = 2, max = 50, message = "Il nome deve essere tra 2 e 50 caratteri")
    private String firstName;

    @Size(min = 2, max = 50, message = "Il cognome deve essere tra 2 e 50 caratteri")
    private String lastName;

    @Size(max = 200, message = "L'indirizzo non può superare i 200 caratteri")
    private String address;

    @Size(max = 200, message = "L'indirizzo email non può superare i 200 caratteri")
    private String email;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Formato telefono non valido")
    private String telephone;

    @Size(min = 8, message = "La password deve avere almeno 8 caratteri")
    // @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
    // message = "La password deve contenere almeno un numero, una lettera
    // minuscola, una maiuscola e un carattere speciale")
    private String newPassword;
}