package com.winderp.authentification.services;

import com.winderp.authentification.Models.User;
import com.winderp.authentification.Repository.UserRepository;
import com.winderp.authentification.config.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    // REGISTER
    public User register(User user){

        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new RuntimeException("Email already exists");
        }

        return userRepository.save(user);
    }

    // LOGIN
    public Map<String,String> login(User request){

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(!user.getPassword().equals(request.getPassword())){
            throw new RuntimeException("Invalid password");
        }

        String role = user.getClass().getSimpleName();

        String token = JwtUtil.generateToken(user.getEmail(), role);

        Map<String,String> response = new HashMap<>();
        response.put("token", token);
        response.put("role", role);
        response.put("email", user.getEmail());

        return response;
    }
}