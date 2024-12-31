package com.ecom.Shopping_Cart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    @Lazy
    private  AuthFailureHandlerImpl authFailureHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(){
        return new UserDetailsServiceImlp();
    }


    //Lớp này được Spring Security sử dụng để xác thực thông tin đăng nhập của người dùng.
    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        //Là một lớp cung cấp cơ chế xác thực dựa trên cơ sở dữ liệu
        // hoặc bất kỳ nguồn nào sử dụng UserDetailsService
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();

        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;

    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //CSRF : Cross-Site Request Forgery         CORS (Cross-Origin Resource Sharing)
        http.csrf(csrf->csrf.disable()).cors(cors->cors.disable())
                .authorizeHttpRequests(red->red.requestMatchers("/user/**").hasRole("USER")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/**").permitAll())
                .formLogin(form->form.loginPage("/signin")
                        .loginProcessingUrl("/login")
//                        .defaultSuccessUrl("/")
                        .failureHandler(authFailureHandler)
                        .successHandler(authenticationSuccessHandler))
                .logout(logout->logout.permitAll());
        return http.build();

    }


}
