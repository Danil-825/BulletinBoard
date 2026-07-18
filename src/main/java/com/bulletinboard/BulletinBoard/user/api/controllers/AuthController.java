package com.bulletinboard.BulletinBoard.user.api.controllers;

import com.bulletinboard.BulletinBoard.user.api.dto.JwtResponse;
import com.bulletinboard.BulletinBoard.user.api.dto.LoginRequest;
import com.bulletinboard.BulletinBoard.user.api.dto.users.RegisterRequest;
import com.bulletinboard.BulletinBoard.user.impl.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@RequestBody RegisterRequest request) {
        JwtResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {
        JwtResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

}
