package com.musiciantrainer.musiciantrainerproject.security;

import com.musiciantrainer.musiciantrainerproject.dao.RememberMeTokenDao;
import com.musiciantrainer.musiciantrainerproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private RememberMeTokenDao rememberMeTokenDao;

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

    @Bean
    public PersistentTokenRepository persistentTokenRepository(DataSource dataSource,
                                                        RememberMeTokenDao rememberMeTokenDao) {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }

    // Tady musím upravit endpointy v uvozovkách, aby souhlasily
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(configurer ->
                        configurer
                                .requestMatchers("/").permitAll()
                                .requestMatchers("/home").hasRole("USER")
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
                                .requestMatchers("/landing_page/**").permitAll()
                                .requestMatchers("/reflection/**").hasRole("USER")
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
                        logout.permitAll()
                )
                .exceptionHandling(configurer ->
                        configurer.accessDeniedPage("/access-denied")
                )
                .rememberMe(remember ->
                        remember.tokenRepository(persistentTokenRepository(dataSource, rememberMeTokenDao))
                )
                        // default expiration time is 14 days)
                ;

        return http.build();
    }
}
