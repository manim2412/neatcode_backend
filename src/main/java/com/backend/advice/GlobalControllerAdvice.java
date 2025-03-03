package com.backend.advice;

//import com.backend.exception.CustomExpiredJwtException;
import com.backend.exception.LoginException;
import com.backend.exception.RefreshTokenException;
import com.backend.exception.RegistrationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalControllerAdvice {
    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<Map<String, String>> registrationException(RegistrationException exception) {
        Map<String, String> map = new HashMap<>();
        map.put("message", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }

    @ExceptionHandler(LoginException.class)
    public ResponseEntity<Map<String, String>> loginException(LoginException exception) {
        Map<String, String> map = new HashMap<>();
        map.put("message", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }

    @ExceptionHandler(RefreshTokenException.class)
    public ResponseEntity<Map<String, String>> refreshTokenException(RefreshTokenException exception) {
        Map<String, String> map = new HashMap<>();
        map.put("message", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }

//    @ExceptionHandler(CustomExpiredJwtException.class)
//    public ResponseEntity<Map<String, String>> customExpiredJwtException(CustomExpiredJwtException exception) {
//        Map<String, String> map = new HashMap<>();
//        map.put("message", exception.getMessage());
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
//    }
}
