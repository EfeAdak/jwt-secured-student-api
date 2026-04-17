package com.efe.apidemo.controller;


import com.efe.apidemo.dto.AuthenticationRequest;
import com.efe.apidemo.dto.AuthenticationResponse;
import com.efe.apidemo.dto.RegisterRequest;
import com.efe.apidemo.service.AuthenticationService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @GetMapping("/test")
    public String test() {
        return "auth is open";
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {

        return ResponseEntity.ok(authenticationService.register(request));
    }


    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {

        return ResponseEntity.ok(authenticationService.login(request));
    }

}
