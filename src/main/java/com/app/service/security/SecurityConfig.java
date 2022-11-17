package com.app.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig //extends WebSecurityConfigurerAdapter
{
//    @Override
//    protected void configure(HttpSecurity http) throws Exception
//    {
//        http
//         .csrf().disable()
//         .authorizeRequests()
//         .antMatchers("/back/serv/test").permitAll()
//         .antMatchers("/back/serv/mbox").permitAll()
//         .antMatchers("/back/serv/mmuser").permitAll()
//         .antMatchers("/back/serv/cduser").permitAll()
//         .anyRequest().authenticated()
//         .and()
//         .httpBasic();
//    }
//
//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth)
//            throws Exception
//    {
//        auth.inMemoryAuthentication()
//        	.withUser("admin")
//        	.password("{noop}admin")
//        	.roles("USER");
//    }
}
