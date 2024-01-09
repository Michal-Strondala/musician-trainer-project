package com.musiciantrainer.musiciantrainerproject.security;

import com.musiciantrainer.musiciantrainerproject.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //bcrypt bean definition
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //authenticationProvider bean definition
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserService userService) {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userService); //set the custom user details service
        auth.setPasswordEncoder(passwordEncoder()); //set the password encoder - bcrypt
        return auth;
    }

    // Tady musím upravit endpointy v uvozovkách, aby souhlasily
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(configurer ->
                        configurer
                                .requestMatchers("/").hasRole("USER")
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .requestMatchers("/register/**").permitAll()
                                .requestMatchers("/password/**").permitAll()
                                .requestMatchers("/user/**").hasRole("USER")
                                .requestMatchers("/user/showFormForEdit").hasRole("USER")
                                .requestMatchers("/pieces/**").hasRole("USER")
                                .requestMatchers("/myplan/**").hasRole("USER")
                                .requestMatchers("/sendEmail/**").permitAll()
                                .requestMatchers("/dist/**").permitAll()
                                .requestMatchers("/assets/**").permitAll()
                                .anyRequest().authenticated()
                // musím přehodit všechny metody z usercontroller do asi jiného controlleru a pak přidat sem další povolení
                )
                .formLogin(form ->
                        form
                                .loginPage("/showLoginPage")
                                .loginProcessingUrl("/authenticateTheUser")
                                .permitAll()
                )
                .logout(logout ->
                        logout
                                .permitAll()
                )
                .exceptionHandling(configurer ->
                        configurer.accessDeniedPage("/access-denied")
                );

        return http.build();
    }
}
