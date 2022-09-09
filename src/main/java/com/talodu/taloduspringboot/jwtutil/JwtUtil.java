package com.talodu.taloduspringboot.jwtutil;

import com.talodu.taloduspringboot.model.MyUserDetails;
import com.talodu.taloduspringboot.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtUtil {
    private String SECRET_KEY = "jtjwtsecret";

    public String extractUsername(String token) {

        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);

    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {

        // return extractExpiration(token).before(new Date());
        return false;
    }

    public String generateToken(MyUserDetails userDetails, Integer duration_minutes) {
        Map<String, Object> claims = new HashMap<>();
       // Collection<String> roles = new ArrayList<>();
        //roles =  userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
       return createToken(claims, userDetails.getUsername(),duration_minutes);
        //return createToken(roles, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject,Integer duration_minutes) {
       // private String createToken(Collection<String> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
            //return Jwts.builder().w(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60*60*duration_minutes))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    public Boolean validateToken(String token, MyUserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }


}
