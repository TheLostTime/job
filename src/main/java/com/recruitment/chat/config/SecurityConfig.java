package com.recruitment.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;
import java.util.List;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "/index.html", "/static/**", "/ws/**", "/login.html", "/test.html").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                .successHandler((request, response, auth) -> {
                    System.out.println("登录成功！用户: " + auth.getName());
                    response.sendRedirect("/test.html");
                })
                .failureHandler((request, response, ex) -> {
                    System.out.println("登录失败！原因: " + ex.getMessage());
                    response.sendRedirect("/login.html?error");
                })
                .permitAll()
                .and()
                .logout()
                .permitAll();

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // 定义第一个用户，角色为 USER
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("wk")
                .password("wk")
                .roles("USER")
                .build();

        // 定义第二个用户，角色为 ADMIN
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("wk1")
                .password("wk1")
                .roles("ADMIN")
                .build();

        // 可以根据需要添加更多用户

        // 创建一个包含所有用户的列表
        List<UserDetails> users = Arrays.asList(user, admin);

        // 返回一个 InMemoryUserDetailsManager，其中包含所有用户
        return new InMemoryUserDetailsManager(users);
    }
}