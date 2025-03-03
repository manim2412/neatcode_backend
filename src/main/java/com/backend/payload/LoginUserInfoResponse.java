package com.backend.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUserInfoResponse {
    private String username;
    private String email;
}
