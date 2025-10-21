package account.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "MySuperSecretKeyForJwtExample12345"; // same across services

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // ✅ Extract username (also validates signature)
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token) // this validates the token signature
                .getBody()
                .getSubject();
    }

    // ✅ Validate token properly
    public boolean validateToken(String token) {
        try {
            // 🔥 This line throws exceptions for invalid/malformed tokens
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);

            System.out.println("✅ Token is valid");
            return true;

        } catch (ExpiredJwtException e) {
            System.out.println("⚠️ Token expired: " + e.getMessage());
            throw new JwtException("Token expired");
        } catch (UnsupportedJwtException e) {
            System.out.println("⚠️ Unsupported JWT: " + e.getMessage());
            throw new JwtException("Unsupported JWT token");
        } catch (MalformedJwtException e) {
            System.out.println("⚠️ Malformed token: " + e.getMessage());
            throw new JwtException("Invalid JWT structure");
        } catch (SignatureException e) {
            System.out.println("⚠️ Invalid signature: " + e.getMessage());
            throw new JwtException("Invalid JWT signature");
        } catch (IllegalArgumentException e) {
            System.out.println("⚠️ Empty or null token: " + e.getMessage());
            throw new JwtException("Token cannot be null or empty");
        }
    }
}