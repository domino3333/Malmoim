package com.malmoim.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password4j.BcryptPassword4jPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http){
        return http
                .formLogin(fr->fr.disable())
                .httpBasic(hb->hb.disable())
                .csrf(csrf->csrf.disable())
                .authorizeHttpRequests(auth->auth
                                .requestMatchers("/api/member/").permitAll()

                )
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    public AuthenticationManager provideAuthManager(AuthenticationConfiguration ac) throws Exception{
        return ac.getAuthenticationManager();
    }

    @Bean
    BCryptPasswordEncoder providePasswordEncoder(){
        return new BCryptPasswordEncoder();
    }


}
