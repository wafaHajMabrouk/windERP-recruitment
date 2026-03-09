package com.winderp.authentification.Controllers;

import com.winderp.authentification.Models.User;
import com.winderp.authentification.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;

    // REGISTER
    @PostMapping("/register")
    public User register(@RequestBody User user){
        return authService.register(user);
    }

    // LOGIN
    @PostMapping("/login")
    public Map<String,String> login(@RequestBody User user){
        return authService.login(user);
    }
}