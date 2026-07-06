package com.example.backend.service;
import com.example.backend.config.SecurityConfig;
import com.example.backend.dto.UserDto;
import com.example.backend.dto.custom.LoginDto;
import com.example.backend.dto.custom.LoginResponseDto;
import com.example.backend.dto.custom.ResponseDto;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Optional;

@Service
public class UserService {

    private static final String MSG_CODE_1 = "SOME IMPORTANT FIELDS ARE NULL!!!";
    private static final String MSG_CODE_2 = "SOME IMPORTANT FIELDS ARE MISSING!!!";
    private static final String MSG_CODE_3 = "USER REGISTRATION FAILED";
    private static final String MSG_CODE_4 = "USER REGISTRATION SUCCESSFUL";
    private final UserRepo userRepo;
    private final SecurityConfig securityConfig;
    private final RefreshTokenService refreshTokenService;

    public UserService(UserRepo userRepo, SecurityConfig securityConfig, RefreshTokenService refreshTokenService) {
        this.userRepo = userRepo;
        this.securityConfig = securityConfig;
        this.refreshTokenService = refreshTokenService;
    }

    public ResponseEntity signUp(UserDto userDto) throws NoSuchAlgorithmException {
        ResponseDto response = new ResponseDto();
        if (userDto.getEmail() == null || userDto.getPassword() == null || userDto.getUsername() == null || userDto.getRole() == null) {
            return ResponseEntity.badRequest().body(MSG_CODE_1);
        }
        if (userDto.getEmail().equals("") || userDto.getPassword().equals("") || userDto.getUsername().equals("") || userDto.getRole().equals("")) {
            return ResponseEntity.badRequest().body(MSG_CODE_2);
        }

        // Validate role — now supports ADMIN, USER, SELLER
        String role = userDto.getRole().toUpperCase();
        if (!role.equals("ADMIN") && !role.equals("USER") && !role.equals("SELLER")) {
            return ResponseEntity.badRequest().body("Invalid role. Allowed roles: ADMIN, USER, SELLER");
        }

        String encPassword = securityConfig.hashPassword(userDto.getPassword());

        System.out.println("ENC PASSWORD : " + encPassword);

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername());
        user.setRole(role);
        user.setPassword(encPassword);
        user.setFullName(userDto.getFullName());
        // New fields with defaults
        user.setStatus("ACTIVE");
        user.setEmailVerified(false);
        user.setCreatedDate(new Date());

        User savedUser = userRepo.save(user);
        if (savedUser.getUserId() != null) {
            response.setMessage(MSG_CODE_4);
            response.setData(savedUser);
            return ResponseEntity.ok().body(response);
        } else {
            return ResponseEntity.badRequest().body(MSG_CODE_3);
        }

    }

    public ResponseEntity signIn(LoginDto loginDto) throws NoSuchAlgorithmException {
        ResponseDto response = new ResponseDto();

        if (loginDto.getUsername() == null || loginDto.getPassword() == null) {
            return ResponseEntity.badRequest().body("USERNAME OR PASSWORD NULL");
        } else if (loginDto.getUsername().equals("") || loginDto.getPassword().equals("")) {
            return ResponseEntity.badRequest().body("USERNAME OR PASSWORD EMPTY");
        }

        Optional<User> optUser = userRepo.findUserByUsername(loginDto.getUsername());
        if (optUser.isPresent()) {
            User user = optUser.get();

            // Check if user account is active
            if (user.getStatus() != null && !user.getStatus().equals("ACTIVE")) {
                return ResponseEntity.badRequest().body("Account is " + user.getStatus().toLowerCase() + ". Please contact support.");
            }

            Boolean flag = securityConfig.comparePassword(user.getPassword(), loginDto.getPassword());
            if (flag) {
                // ─── Auto-migrate MD5 hash to BCrypt on successful login ───
                if (securityConfig.isMd5Hash(user.getPassword())) {
                    String bcryptHash = securityConfig.hashPassword(loginDto.getPassword());
                    user.setPassword(bcryptHash);
                    System.out.println("AUTO-MIGRATED password for user: " + user.getUsername() + " from MD5 to BCrypt");
                }

                // Update last login date
                user.setLastLoginDate(new Date());
                userRepo.save(user);

                LoginResponseDto loginResponse = new LoginResponseDto();
                String jwtToken = securityConfig.generateToken(user.getUsername(), user.getUserId(), user.getRole());
                com.example.backend.model.RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUserId());
                loginResponse.setToken(jwtToken);
                loginResponse.setRefreshToken(refreshToken.getToken());

                response.setMessage("LOGIN SUCCESSFUL");
                response.setData(loginResponse);
                return ResponseEntity.ok().body(response);
            } else {
                return ResponseEntity.badRequest().body("PASSWORD INCORRECT");
            }
        } else {
            return ResponseEntity.badRequest().body("USER NOT FOUND");
        }
    }

    public ResponseEntity getProfile(Integer userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().body("USER ID NULL");
        }
        Optional<User> optUser = userRepo.findById(userId);
        ResponseDto response = new ResponseDto();
        if (optUser.isPresent()) {
            User user = optUser.get();
            UserDto userDto = new UserDto();
            userDto.setUserId(user.getUserId());
            userDto.setEmail(user.getEmail());
            userDto.setFullName(user.getFullName());
            userDto.setRole(user.getRole());
            userDto.setPassword(null); // omit password
            userDto.setUsername(user.getUsername());
            userDto.setProfileImageUrl(user.getProfileImageUrl());
            userDto.setPhoneNumber(user.getPhoneNumber());
            userDto.setAddress(user.getAddress());

            response.setMessage("USER FOUND");
            response.setData(userDto);
            return ResponseEntity.ok().body(response);
        } else {
            return ResponseEntity.badRequest().body("USER NOT FOUND");
        }
    }

    public ResponseEntity getAllUsers() {
        ResponseDto response = new ResponseDto();
        java.util.List<User> users = userRepo.findAll();
        users.forEach(u -> u.setPassword(null)); // blank passwords for security
        response.setMessage("All users retrieved successfully");
        response.setData(users);
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity updateProfile(UserDto userDto) {
        ResponseDto response = new ResponseDto();
        if (userDto.getUserId() == null) {
            return ResponseEntity.badRequest().body("User ID is required");
        }
        Optional<User> optUser = userRepo.findById(userDto.getUserId());
        if (optUser.isPresent()) {
            User user = optUser.get();
            user.setFullName(userDto.getFullName());
            user.setEmail(userDto.getEmail());
            user.setPhoneNumber(userDto.getPhoneNumber());
            user.setAddress(userDto.getAddress());
            if (userDto.getProfileImageUrl() != null) {
                user.setProfileImageUrl(userDto.getProfileImageUrl());
            }
            userRepo.save(user);

            UserDto updatedDto = new UserDto();
            updatedDto.setUserId(user.getUserId());
            updatedDto.setFullName(user.getFullName());
            updatedDto.setEmail(user.getEmail());
            updatedDto.setUsername(user.getUsername());
            updatedDto.setRole(user.getRole());
            updatedDto.setProfileImageUrl(user.getProfileImageUrl());
            updatedDto.setPhoneNumber(user.getPhoneNumber());
            updatedDto.setAddress(user.getAddress());

            response.setMessage("Profile updated successfully");
            response.setData(updatedDto);
            return ResponseEntity.ok().body(response);
        } else {
            return ResponseEntity.badRequest().body("User not found");
        }
    }
}