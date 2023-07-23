package kr.ac.cbnu.tux.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import io.micrometer.common.util.StringUtils;
import org.springframework.security.core.Authentication;

import java.security.Key;
import java.util.Date;

public class JwtTokenProvider {

    private static final String JWT_SECRET = "wewklfjlwejfl;wjeflwejfl;kjsdc;ljeilwjrhfl;iwejf;lawjklfehklfhwkjehfkjer";
    private static final Key JWT_SECRET_KEY = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());

    // Token Expiration Time
    private static final int JWT_EXPIRATION_MS = 168 * 60 * 60 * 1000;

    private final static JwtParser jwtParser;

    static {
        jwtParser = Jwts.parserBuilder()
                .setSigningKey(JWT_SECRET_KEY)
                .build();
    }

    public static String generateToken(Authentication authentication) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_MS);

        return Jwts.builder()
                .setSubject((String) authentication.getPrincipal())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(JWT_SECRET_KEY, SignatureAlgorithm.HS512)
                .compact();
    }

    public static String getUsernameFromJwt(String token) {
        Claims claims = jwtParser.parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public static boolean validateToken(String token) {
        if (token == null || StringUtils.isEmpty(token))
            return false;

        try {
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            //log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            //log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            //log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            //log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            //log.error("JWT claims string is empty.");
        }
        return false;
    }
}
