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
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final SecurityConfig securityConfig;
    private final com.example.backend.service.RefreshTokenService refreshTokenService;

    public UserController(UserService userService, SecurityConfig securityConfig, com.example.backend.service.RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.securityConfig = securityConfig;
        this.refreshTokenService = refreshTokenService;
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

        Optional<com.example.backend.model.RefreshToken> optToken = refreshTokenService.findByToken(refreshToken);
        if (!optToken.isPresent()) {
            response.setMessage("Invalid refresh token");
            return ResponseEntity.status(401).body(response);
        }

        try {
            com.example.backend.model.RefreshToken token = optToken.get();
            refreshTokenService.verifyExpiration(token);
            com.example.backend.model.User user = token.getUser();

            String newAccessToken = securityConfig.generateToken(user.getUsername(), user.getUserId(), user.getRole());
            com.example.backend.model.RefreshToken rotatedToken = refreshTokenService.createRefreshToken(user.getUserId());

            LoginResponseDto loginResponse = new LoginResponseDto();
            loginResponse.setToken(newAccessToken);
            loginResponse.setRefreshToken(rotatedToken.getToken());

            response.setMessage("Token refreshed successfully");
            response.setData(loginResponse);
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            response.setMessage(e.getMessage());
            return ResponseEntity.status(401).body(response);
        }
    }
}
