package com.kelompok2.remindertugas.util;

import com.kelompok2.remindertugas.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Token {

    private static final String SEPARATOR = ",";

    public static String generateToken(User user) {
        String tokenCreatedAt = Instant.now().toString();
        String tokenExpiredBy = Instant.now().plus(1, ChronoUnit.HOURS).toString();
        String tokenData = String.join(SEPARATOR, String.valueOf(user.getId()),
                String.valueOf(user.getRoles().getId()),
                tokenCreatedAt,
                tokenExpiredBy,
                user.getUsername());
        return Base64.getEncoder().encodeToString(tokenData.getBytes());
    }

    public static String decodeToken(String token) {
        byte[] decodedBytes = Base64.getDecoder().decode(token);
        return new String(decodedBytes);
    }

    public static String getUsernameFromToken(String token) {
        try {
            String decodedToken = decodeToken(token);
            String[] tokenParts = decodedToken.split(SEPARATOR);
            return tokenParts[4];
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean validateToken(String token, UserDetails userDetails) {
        try {
            String decodedToken = decodeToken(token);
            String[] tokenParts = decodedToken.split(SEPARATOR);

            if (tokenParts.length != 5) {
                return false;
            }

            String tokenExpiredBy = tokenParts[3];
            Instant tokenExpirationTime = Instant.parse(tokenExpiredBy);

            return Instant.now().isBefore(tokenExpirationTime);
        } catch (Exception e) {
            return false;
        }
    }

    public static Map<String, String> getTokenData(String token) {
        String decodedToken = decodeToken(token);
        String[] tokenParts = decodedToken.split(SEPARATOR);

        if (tokenParts.length != 5) {
            return null;
        }

        Map<String, String> tokenData = new HashMap<>();
        tokenData.put("userId", tokenParts[0]);
        tokenData.put("roleId", tokenParts[1]);
        return tokenData;
    }
}
