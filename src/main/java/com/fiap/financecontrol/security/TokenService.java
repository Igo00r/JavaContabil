package com.fiap.financecontrol.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;

@Service
public class TokenService {
    @Value(value = "${api.security.token.secret}")
    private String secret;
    public String gerarToken(Long idUsuario, Collection<? extends GrantedAuthority> authorities) {
        try {
            var algoritmo = Algorithm.HMAC256(secret);

            List<String> roles = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            return JWT.create()
                    .withIssuer("JavaContabil")
                    .withSubject(idUsuario.toString())
                    .withClaim("roles", roles)
                    .withExpiresAt(dataExpiracao())
                    .sign(algoritmo);

        } catch (Exception exception) {
            throw new RuntimeException("erro ao gerar token jwt", exception);
        }
    }


    public String getSubject(String token) {
        try {

            DecodedJWT decoded = JWT.decode(token);
            String subject = decoded.getSubject();

            Algorithm algoritmo = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algoritmo)
                    .withIssuer("JavaContabil")
                    .build();

            try {
                verifier.verify(token);
            } catch (TokenExpiredException e) {

            }

            return subject;

        } catch (JWTVerificationException e) {
            throw new RuntimeException("Token inválido!");
        }
    }

    public Instant dataExpiracao() {

        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
