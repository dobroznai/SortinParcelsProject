package idhub.sortinparcels.security;


import idhub.sortinparcels.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;



import java.security.Key;
import java.util.Date;

@Component
public class JwtService {


    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expirationMs}")
    private long jwtExpirationMs;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
    // Генерація токена
    public String generateToken(SortinParcelsSecurityUser userDetails) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);
        User.Role role = userDetails.getRole();


        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("role", role.name())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(),  SignatureAlgorithm.HS256)
                .compact();
    }

    // Парсинг claims Розкриває токен і повертає claims (дані всередині токена).
    private Claims parseClaims(String token) {

        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Витягнути username
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    // Витягнути роль
    public User.Role extractRole(String token) {
        String role = parseClaims(token).get("role", String.class);
        return User.Role.valueOf(role);
    }

    // Перевірка простроченого токена
    private boolean isTokenExpired(String token) {
        Date expiration = parseClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    // Перевірка валідності токена
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
}