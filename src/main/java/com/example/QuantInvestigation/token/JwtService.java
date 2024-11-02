package com.example.QuantInvestigation.token;

import com.example.QuantInvestigation.exception.BaseException;
import com.example.QuantInvestigation.exception.BaseResponseStatus;
import com.example.QuantInvestigation.user.User;
import com.example.QuantInvestigation.user.UserRepository;
import com.example.QuantInvestigation.utils.Secret;
import com.example.QuantInvestigation.utils.UtilService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.Key;

import static com.example.QuantInvestigation.exception.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class JwtService {
    private Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(Secret.JWT_SECRET_KEY));
    private final JwtProvider jwtProvider;
    private final UtilService utilService;
    private final UserRepository userRepository;

    /**
     * Header에서 Authorization 으로 JWT 추출
     */
    public String getJwt(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String authorizationHeader = request.getHeader("Authorization");
        String accessToken = authorizationHeader.split(" ")[1];
        return accessToken;
    }

    /**
     * JWT에서 userId 추출
     */
    public Long getUserIdx() throws BaseException {
        // 1. JWT 추출
        String accessToken = getJwt();
        if (accessToken == null || accessToken.length() == 0) {
            throw new BaseException(EMPTY_JWT);
        }

        try {
            // 2. JWT parsing
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken);
            // 3. userId 추출
            Long userId = claims.getBody().get("userId", Long.class);
            User user = utilService.findByUserIdWithValidation(userId);

            return userId;
        } catch (ExpiredJwtException e) {
            throw new BaseException(EXPIRED_USER_JWT);
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new BaseException(INVALID_JWT);
        } catch (Exception e) {
            throw new BaseException(INVALID_JWT);
        }
    }
}
