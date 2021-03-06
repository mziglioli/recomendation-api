package com.recomendationapi.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class JwtService {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expire.minutes}")
  private int expireMinutes;

  /**
   * Create token from username, {@link System#currentTimeMillis()} and default expired time
   * this{@link #getDateExpired()}
   *
   * @param json
   * @throws Exception if token expired or before date
   * @return string
   */
  public String createToken(String json) {
    return createToken(json, new Date(System.currentTimeMillis()), getDateExpired());
  }

  /**
   * Create token from dto
   *
   * @param json
   * @param nowDate
   * @param expiredDate
   * @throws Exception if token expired or before date
   * @return string
   */
  public String createToken(String json, Date nowDate, Date expiredDate) {
    return Jwts.builder()
        .setSubject(json)
        .setIssuedAt(nowDate)
        .setNotBefore(nowDate)
        .setExpiration(expiredDate)
        .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS512)
        .compact();
  }

  /**
   * Decrypt token
   *
   * @param token jwt token generated by this{@link #createToken(String)}
   * @throws Exception if token expired or before date
   * @return string
   */
  public String decryptToken(String token) throws Exception {
    Claims claims =
        Jwts.parser()
            .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
            .parseClaimsJws(token)
            .getBody();

    if (isTokenBefore(claims)) {
      throw new Exception("Token is before by: " + claims.getNotBefore());
    }
    if (isTokenExpired(claims)) {
      throw new Exception("Token is expired by: " + claims.getExpiration());
    }
    return claims.getSubject();
  }

  private boolean isTokenExpired(Claims claims) {
    return System.currentTimeMillis() > claims.getExpiration().getTime();
  }

  private boolean isTokenBefore(Claims claims) {
    return System.currentTimeMillis() < claims.getNotBefore().getTime();
  }

  /**
   * Get the date that the token will expired in the future. expiredMinutes =
   * ${chat.jwt.expire.minutes} in the file properties This will use System.currentTimeMillis() +
   * TimeUnit.MINUTES.toMillis(expiredMinutes)
   *
   * @return Date in the future
   */
  public Date getDateExpired() {
    return new Date(getExpires());
  }

  public long getExpires() {
    return System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(expireMinutes);
  }
}
