package com.example.backend.controller;

import com.example.backend.dto.UserDto;
import com.example.backend.dto.custom.LoginDto;
import com.example.backend.dto.custom.ResponseDto;
import com.example.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signUp")
    public ResponseEntity<ResponseDto> signUp(@RequestBody UserDto userDto) throws NoSuchAlgorithmException {
        return userService.signUp(userDto);
    }

    @PostMapping("/signIn")
    public ResponseEntity<ResponseDto> signIn(@RequestBody LoginDto loginDto) throws NoSuchAlgorithmException {
        return userService.signIn(loginDto);
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<ResponseDto> getProfile(@PathVariable Integer userId) {
        return userService.getProfile(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateProfile(@RequestBody UserDto userDto) {
        return userService.updateProfile(userDto);
    }
}
