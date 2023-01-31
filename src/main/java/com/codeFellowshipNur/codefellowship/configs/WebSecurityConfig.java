package com.codeFellowshipNur.codefellowship.configs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// TODO Step 5: Create the WebSecruityConfig
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    //TODO: wire up the ApplicationUserDetailsServiceImpl
    @Autowired
    ApplicationUserDetailsServiceImpl applicationUserDetailsService;
    // passwordEncoder bean
    @Bean
    public PasswordEncoder passwordEncoder(){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }

    //Configure AuthenticationManagerBuilder
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // BUILDER PATTERN Method().config().config.config()
        auth.userDetailsService(applicationUserDetailsService).passwordEncoder(passwordEncoder());
    }

    //Configure HttpSecurity
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().disable() // CROSS ORIGIN RESOURCE SHARING
                .csrf().disable()
                // REQUEST SECTION
                .authorizeRequests()
                .antMatchers("/", "/login", "/signup").permitAll()
                .anyRequest().authenticated()
                .and() // Seperator
                // Login Section
                .formLogin()
                .loginPage("/login")
                .and()
                // Logout section
                .logout()
                .logoutSuccessUrl("/login");
    }

}
