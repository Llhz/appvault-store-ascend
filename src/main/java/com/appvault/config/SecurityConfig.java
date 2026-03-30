package com.appvault.config;

import com.appvault.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/", "/browse/**", "/app/**", "/search/**", "/auth/**",
                             "/css/**", "/js/**", "/images/**", "/h2-console/**").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user/**", "/review/**").authenticated()
                .anyRequest().authenticated()
            .and()
            .formLogin()
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/auth/login?error")
                .permitAll()
            .and()
            .logout()
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/?logout")
                .permitAll()
            .and()
            .rememberMe()
                .tokenValiditySeconds(14 * 24 * 60 * 60)
                .key("appVaultRememberMe")
                .userDetailsService(userDetailsService)
            .and()
            .csrf()
                .ignoringAntMatchers("/h2-console/**")
            .and()
            .headers()
                .frameOptions().sameOrigin();
    }
}
