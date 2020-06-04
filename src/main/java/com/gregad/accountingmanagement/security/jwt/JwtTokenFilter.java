package com.gregad.accountingmanagement.security.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;

// all new request if token expiration still valid we extend its validity
public class JwtTokenFilter extends GenericFilterBean {
   
    private JwtTokenProvider jwtTokenProvider;
    
    
    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String token=jwtTokenProvider.resolveToken((HttpServletRequest) servletRequest);
        if (token!=null && jwtTokenProvider.validateToken(token)){
            String newToken=jwtTokenProvider.updateToken(token);
            jwtTokenProvider.updateHeaderRequest((HttpServletResponse) servletResponse,newToken);
               Authentication authentication=jwtTokenProvider.getAuthentication(newToken);
                if (authentication!=null){
                    SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(servletRequest,servletResponse);

    }
}
