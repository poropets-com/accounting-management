package com.gregad.accountingmanagement.security.jwt;

import com.gregad.accountingmanagement.dto.responseDto.ValidateTokenResponseDto;
import com.gregad.accountingmanagement.security.JwtUserDetailsService;
import com.gregad.accountingmanagement.service.interfaces.IUserTokenService;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {
    private static final String HEADER ="Authorization" ;
    private static final String PREFIX = "Bearer ";
    @Value("${jwt.token.secret}")
    private String secret;
    @Value("${jwt.token.expired}")
    private long validityInMilliseconds;
    
    @Autowired
    private JwtUserDetailsService userDetailsService;
    @Autowired
    private IUserTokenService userTokenService;
    @Bean
   public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @PostConstruct
    protected void init(){
        secret= Base64.getEncoder().encodeToString(secret.getBytes());
    }

    public String createToken(String email, List<String> roles){
        userTokenService.deleteToken(email);
        Claims claims= Jwts.claims().setSubject(email);
        claims.put("roles",roles);
        claims.put("blocked",false);

        Date now=new Date();
        Date validity=new Date(now.getTime()+validityInMilliseconds);
        String token= Jwts.builder().
                setClaims(claims).
                setIssuedAt(now).
                setExpiration(validity).
                signWith(SignatureAlgorithm.HS256,secret).
                compact();
        userTokenService.addToken(email,token);
        return token;
    }
   
    public Authentication getAuthentication(String token){
        UserDetails userDetails=this.userDetailsService.loadUserByUsername(getUserEmail(token));
        return new UsernamePasswordAuthenticationToken(userDetails,userDetails,userDetails.getAuthorities());
    }
    
    public String getUserEmail(String token){
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }
  
    public String resolveToken(HttpServletRequest request){
        String bearerToken=request.getHeader(HEADER);
        if (bearerToken!=null && bearerToken.startsWith(PREFIX)){
            return bearerToken.substring(7,bearerToken.length()) ;
        }
        return null;
    }
    public boolean validateToken(String token){
        try {
            Jws<Claims>claims=Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            String email = claims.getBody().getSubject();
            if (claims.getBody().get("blocked",Boolean.class)){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Username: "+email+" blocked to use service");
            }
            if (!userTokenService.tokenValidation(email,token)){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,"JWT token is expired or invalid");
            }
            
            if (claims.getBody().getExpiration().before(new Date())){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,"JWT token is expired or invalid");
            }
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.PROXY_AUTHENTICATION_REQUIRED,"JWT token is expired or invalid");
        }
    }
    public String updateToken(String token){
        List<String> roles= getRoles(token);
        return createToken(getUserEmail(token),roles);
    }

    private List<String> getRoles(String token) {
        return (List<String>) Jwts.parser().
                setSigningKey(secret).
                parseClaimsJws(token).
                getBody().get("roles");
    }

    public void updateHeaderResponse(HttpServletResponse response, String newToken){
        response.addHeader(HEADER,PREFIX+newToken);
    }
    
    public ValidateTokenResponseDto getUserData(String token){
        String email=getUserEmail(token);
        List<String> roles= getRoles(token);
        return new ValidateTokenResponseDto(email,roles);
    }
}
