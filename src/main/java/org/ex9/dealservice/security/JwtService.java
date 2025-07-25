package org.ex9.dealservice.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;
import java.util.function.Function;

/**
 * Сервис для работы с JWT-токенами.
 * @author Краковцев Артём
 */
@Service
@Log4j2
public class JwtService {

    /**
     * Секретный ключ для подписи токена.
     */
    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * Извлекает логин пользователя из токена.
     *
     * @param token JWT-токен.
     * @return Логин пользователя.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parse(token);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    /**
     * Извлекает роли пользователя из токена.
     *
     * @param token JWT-токен.
     * @return Список ролей пользователя.
     */
    public List<String> getRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", List.class));
    }

    private  <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Извлекает claims из JWT-токена.
     *
     * @param token JWT-токен.
     * @return Объект Claims с данными токена.
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Генерирует ключ используемый в jwt-токене.
     *
     * @return ключ токена.
     */
    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

}
