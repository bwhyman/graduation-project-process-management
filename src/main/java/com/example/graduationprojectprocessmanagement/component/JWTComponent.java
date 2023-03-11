package com.example.graduationprojectprocessmanagement.component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.graduationprojectprocessmanagement.exception.Code;
import com.example.graduationprojectprocessmanagement.exception.XException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@Component
public class JWTComponent {
    // 私钥
    @Value("${my.secretkey}")
    private String secretkey;
    public String encode(Map<String, Object> map) {
        LocalDateTime time = LocalDateTime.now().plusMonths(1);
        return JWT.create()
                .withPayload(map)
                .withIssuedAt(new Date())
                .withExpiresAt(Date.from(time.atZone(ZoneId.systemDefault()).toInstant()))
                .sign(Algorithm.HMAC256(secretkey));
    }

    public DecodedJWT decode(String token) {
        try {
            return JWT.require(Algorithm.HMAC256(secretkey)).build().verify(token);
        } catch (TokenExpiredException | SignatureVerificationException | JWTDecodeException e) {
            Code code = e instanceof TokenExpiredException ? Code.TOKEN_EXPIRED : Code.FORBIDDEN;
            throw new XException(code);
        }
    }
}
