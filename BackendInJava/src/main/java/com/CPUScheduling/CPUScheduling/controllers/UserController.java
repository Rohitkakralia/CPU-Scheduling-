package com.CPUScheduling.CPUScheduling.controllers;

import com.CPUScheduling.CPUScheduling.entities.User;
import com.CPUScheduling.CPUScheduling.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("UserSignUp")
@CrossOrigin(origins = "*") // Allow requests from any origin (for development)
public class UserController {
    @Autowired
    UserServices userServices;

    @PostMapping()
    public ResponseEntity<Object> addUser(@RequestBody User user){
        try {
            userServices.addUser(user);
            return ResponseEntity.ok().body(
                    Map.of("success", true, "message", "User registered successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", e.getMessage())
            );
        }
    }
}