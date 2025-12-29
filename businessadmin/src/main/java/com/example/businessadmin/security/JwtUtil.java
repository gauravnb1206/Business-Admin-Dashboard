package com.example.businessadmin.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    //secret Key
    private static final String SECRET_KEY = "business-admin-dashboard-shri-chips-027744##@@";

    //Token Validity
    private  static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());


    //Generate Jwt
    public String generateToken(String username){
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Extract username
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    //Validate Token
    public  boolean isTokenValid(String token){
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e){
            return  false;
        }
    }

    public Claims parseClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
