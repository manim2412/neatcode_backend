package com.backend.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RegistrationRequest {
    @Email(message = "registration request email should ALLOW EMAIL FORMAT")
    @NotEmpty(message = "registration request email should NOT EMPTY")
    private String email;

    @NotEmpty(message = "registration request username should NOT EMPTY")
    private String username;

    @NotEmpty(message = "registration request password should NOT EMPTY")
    private String password;
}
