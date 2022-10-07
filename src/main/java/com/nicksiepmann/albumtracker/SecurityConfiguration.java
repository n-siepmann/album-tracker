/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nicksiepmann.albumtracker;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 *
 * @author Nick.Siepmann
 */
@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/oauth2/**", "/error", "/welcome", "/static/**", "/*.css", "/favicon*.png", "/android*.png").permitAll()
                .anyRequest().authenticated()
                .and().oauth2Login().loginPage("/welcome").defaultSuccessUrl("/", true);
        return http.build();

    }

}
