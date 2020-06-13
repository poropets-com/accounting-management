package com.gregad.accountingmanagement.configuration;

import com.gregad.accountingmanagement.security.jwt.JwtConfigurer;
import com.gregad.accountingmanagement.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import static com.gregad.accountingmanagement.api.ApiConstants.*;
import static com.gregad.accountingmanagement.model.Role.*;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }
    
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
    @Override
    protected void configure(HttpSecurity httpSecurity)throws Exception{
        httpSecurity.httpBasic().disable().
                csrf().disable().
                sessionManagement().
                sessionCreationPolicy(SessionCreationPolicy.STATELESS).
                and().
                authorizeRequests().
                antMatchers(HttpMethod.POST,PREFIX).permitAll().
                antMatchers(HttpMethod.POST,PREFIX+LOGIN).permitAll().
                antMatchers(HttpMethod.GET,PREFIX+"/**"+VALIDATION).permitAll();
                
               httpSecurity.authorizeRequests().antMatchers(HttpMethod.GET,PREFIX+"/**"+INFO).hasRole(USER).
                antMatchers(HttpMethod.PUT,PREFIX+"/*").hasRole(USER).
                antMatchers(HttpMethod.DELETE,PREFIX+"/**").hasRole(USER).
                antMatchers(HttpMethod.PUT,PREFIX+"/**"+FAVORITE+"/**").hasRole(USER).
                antMatchers(HttpMethod.DELETE,PREFIX+"/**"+FAVORITE+"/**").hasRole(USER).
                antMatchers(HttpMethod.GET,PREFIX+"/**"+FAVORITES).hasRole(USER).
                 
                antMatchers(HttpMethod.PUT,
                        PREFIX+"/**"+BLOCK+"/**").hasRole(MODERATOR).
               
                antMatchers(HttpMethod.PUT,
                        PREFIX+"/*"+ROLE+"/**").hasRole(ADMIN).
                antMatchers(HttpMethod.DELETE,
                        PREFIX+"/*"+ROLE+"/**").hasRole(ADMIN).
               
                anyRequest().
                authenticated().
                and().
                apply(new JwtConfigurer(jwtTokenProvider));
    }
}
