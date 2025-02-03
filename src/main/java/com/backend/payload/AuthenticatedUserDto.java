package com.backend.payload;

import com.backend.constant.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthenticatedUserDto {
    private String username;
    private String email;
    private UserRole userRole;
    private String password;
}
