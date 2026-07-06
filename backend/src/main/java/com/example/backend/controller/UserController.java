package com.example.backend.controller;

import com.example.backend.config.SecurityConfig;
import com.example.backend.dto.UserDto;
import com.example.backend.dto.custom.LoginDto;
import com.example.backend.dto.custom.JwtDataDto;
import com.example.backend.dto.custom.LoginResponseDto;
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
    private final SecurityConfig securityConfig;

    public UserController(UserService userService, SecurityConfig securityConfig) {
        this.userService = userService;
        this.securityConfig = securityConfig;
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

    /**
     * Refresh an expired access token using a valid refresh token.
     * The refresh token itself must not be expired.
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<ResponseDto> refreshToken(@RequestParam String refreshToken) {
        ResponseDto response = new ResponseDto();

        if (!securityConfig.verifyToken(refreshToken)) {
            response.setMessage("Invalid or expired refresh token");
            return ResponseEntity.status(401).body(response);
        }

        if (!securityConfig.isRefreshToken(refreshToken)) {
            response.setMessage("Provided token is not a refresh token");
            return ResponseEntity.badRequest().body(response);
        }

        JwtDataDto data = securityConfig.getJWTData(refreshToken);
        String newAccessToken = securityConfig.generateToken(data.getUsername(), data.getUserId(), data.getRole());
        String newRefreshToken = securityConfig.generateRefreshToken(data.getUsername(), data.getUserId(), data.getRole());

        LoginResponseDto loginResponse = new LoginResponseDto();
        loginResponse.setToken(newAccessToken);

        response.setMessage("Token refreshed successfully");
        response.setData(loginResponse);
        return ResponseEntity.ok().body(response);
    }
}
