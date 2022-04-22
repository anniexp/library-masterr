/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.securityTest;

/**
 *
 * @author Todor
 */
import javax.sql.DataSource;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.*;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
 
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
@EnableWebSecurity(debug = false)
@EnableWebMvc
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
 
    @Autowired
    private DataSource dataSource;
     
    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().passwordEncoder(new BCryptPasswordEncoder())
            .dataSource(dataSource)
            .usersByUsernameQuery("select username, password, enabled from users where username=?")
            .authoritiesByUsernameQuery("select username, role from users where username=?");
    }
 
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().authorizeRequests()
                        .antMatchers("/register","/registration-part-1", "/registration-part-2","/resources/**", "/static/**","*/css/**","/css/**","/webjars/**","/post_register", "/css/**","/javascript/**").permitAll()

            .anyRequest().authenticated()
            .and()
           // .formLogin().permitAll()
                .formLogin().loginPage("/login").permitAll()

            .and()
            .logout().permitAll()
            .and()
            .exceptionHandling().accessDeniedPage("/accessDenied.html").and().httpBasic();  
    }
    
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
    }
    
   

}
