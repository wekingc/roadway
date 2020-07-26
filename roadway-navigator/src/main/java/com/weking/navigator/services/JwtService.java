package com.weking.navigator.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.weking.navigator.properties.JwtProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;

/**
 * @author Jim Cen
 * @date 2020/7/7 14:11
 */
@Service
public class JwtService {
    @Autowired
    JwtProperty jwtProperty;

    public  String sign(String name){
        Date now = new Date(System.currentTimeMillis());
        Date date = new Date(now.getTime() + jwtProperty.getExpire() * 1000);
        Algorithm algorithm = Algorithm.HMAC256(jwtProperty.getSecret());
        HashMap<String, Object> header = new HashMap<>(4);
        header.put("typ", "JWT");
        header.put("alg", "HS256");
        return JWT.create()
                .withHeader(header)
                .withClaim("name",name)
                .withNotBefore(now)
                .withExpiresAt(date)
                .withIssuer(jwtProperty.getIssuer())
                .withAudience(jwtProperty.getAudience())
                .sign(algorithm);
    }

    public boolean authenticate(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtProperty.getSecret());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        } catch (JWTVerificationException e) {
            return false;
        }

    }
}
