package com.example.backend.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.backend.dto.custom.JwtDataDto;
import org.json.JSONObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.NotNull;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;


@Configuration
public class SecurityConfig {

    private static final String secret = "1234567891011121314151617181920";

    // JWT token validity: 24 hours
    private static final long ACCESS_TOKEN_VALIDITY_MS = 24 * 60 * 60 * 1000L;

    // JWT refresh token validity: 7 days
    private static final long REFRESH_TOKEN_VALIDITY_MS = 7 * 24 * 60 * 60 * 1000L;

    private final BCryptPasswordEncoder bCryptEncoder = new BCryptPasswordEncoder(12);

    // ─── JWT Token Methods ──────────────────────────────────────

    public String generateToken(String userName, Integer userId, String role) {

        if (userName == null || userName.equals("")) {
            return "USERNAME NULL";
        }
        if (userId == null) {
            return "USER_ID NULL";
        }
        if (role == null) {
            return "ROLE NULL";
        }
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("auth2")
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_MS))
                    .withClaim("username", userName)
                    .withClaim("userId", userId)
                    .withClaim("role", role)
                    .sign(algorithm);
            return token;
        } catch (Exception e) {
            e.printStackTrace();
            e.getMessage();
            return e.getMessage();
        }
    }

    /**
     * Generates a refresh token with longer validity (7 days).
     */
    public String generateRefreshToken(String userName, Integer userId, String role) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("auth2")
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY_MS))
                    .withClaim("username", userName)
                    .withClaim("userId", userId)
                    .withClaim("role", role)
                    .withClaim("type", "refresh")
                    .sign(algorithm);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public boolean verifyToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            verifier.verify(decodedJWT);
            return true;
        } catch (JWTVerificationException exception) {
            System.out.println(exception.getMessage());
            return false;
        }
    }

    /**
     * Checks if a token is a refresh token.
     */
    public boolean isRefreshToken(String token) {
        try {
            DecodedJWT decoded = JWT.decode(token);
            String type = decoded.getClaim("type").asString();
            return "refresh".equals(type);
        } catch (Exception e) {
            return false;
        }
    }

    // ─── Password Hashing Methods ───────────────────────────────

    /**
     * Hashes a password using BCrypt (replaces MD5).
     */
    public String hashPassword(@NotNull String password) throws NoSuchAlgorithmException {
        return bCryptEncoder.encode(password);
    }

    /**
     * Legacy MD5 hash method — kept for migration detection only.
     */
    private String hashPasswordMd5(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder(digest.length * 2);
        for (byte b : digest) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    /**
     * Compares a stored hash against a plaintext password.
     *
     * Migration-safe: if the stored hash is an old MD5 hash (32-char uppercase hex),
     * it compares using MD5. The caller (UserService) is responsible for
     * detecting this case and re-hashing with BCrypt.
     *
     * @return true if the password matches the hash
     */
    public Boolean comparePassword(@NotNull String hash, @NotNull String password) throws NoSuchAlgorithmException {
        if (isMd5Hash(hash)) {
            // Legacy MD5 comparison
            String md5Hash = hashPasswordMd5(password);
            return hash.equals(md5Hash);
        } else {
            // BCrypt comparison
            return bCryptEncoder.matches(password, hash);
        }
    }

    /**
     * Detects if a stored hash is a legacy MD5 hash.
     * MD5 hashes are exactly 32 characters of uppercase hexadecimal.
     */
    public boolean isMd5Hash(String hash) {
        return hash != null && hash.length() == 32 && hash.matches("^[0-9A-F]+$");
    }

    // ─── JWT Data Extraction ────────────────────────────────────

    public JwtDataDto getJWTData(String token) {
        JwtDataDto jwtDataDto = new JwtDataDto();

        // SECURITY FIX: previously this method decoded the JWT payload directly
        // without ever checking the signature, so any caller could forge a token
        // (e.g. base64-encode {"userId":1,"role":"ADMIN"}) and be trusted as a
        // real, logged-in admin. Every call site already treats a null userId as
        // "not authenticated", so verifying here and returning an empty DTO on
        // failure fixes it everywhere without touching each service.
        if (token == null || token.isEmpty() || !verifyToken(token)) {
            return jwtDataDto;
        }

        try {
            String[] tokenParts = token.split("\\.");
            String payload = tokenParts[1];

            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payloadJson = new String(decoder.decode(payload));
            JSONObject data = new JSONObject(payloadJson);

            jwtDataDto.setUsername(data.get("username").toString());
            jwtDataDto.setUserId(Integer.parseInt(data.get("userId").toString()));
            jwtDataDto.setIss(data.get("iss").toString());
            if (data.has("role")) {
                jwtDataDto.setRole(data.get("role").toString());
            }
        } catch (Exception e) {
            // Malformed token: return an empty DTO (userId null) rather than throwing,
            // so callers' existing "userId == null" checks reject it safely.
            return new JwtDataDto();
        }
        return jwtDataDto;
    }
}
