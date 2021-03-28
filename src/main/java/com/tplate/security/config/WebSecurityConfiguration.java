package com.tplate.security.config;

import com.tplate.handlers.JwtExceptionHandlerFilter;
import com.tplate.security.jwt.JwtAuthorizationFilter;
import com.tplate.security.jwt.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    JwtAuthorizationFilter jwtAuthorizationFilter;

    @Autowired
    JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    JwtExceptionHandlerFilter jwtExceptionHandlerFilter;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // configure AuthenticationManager so that it knows from where to load
        // user for matching credentials
        // Use BCryptPasswordEncoder
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // We don't need CSRF for this example
        http.cors().and().csrf().disable();

        // Allow same origin for X-Frame-Options on the server
        http.headers().frameOptions().sameOrigin();

        //Permit request from diferent origins, equivalent to: @CrossOrigin
        http.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues());

        //Sessions are management by token
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        //Public and private Paths
        http.authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "*").permitAll()
                .antMatchers("/api/security/login").permitAll()
                .antMatchers("/api/security/sign-up").permitAll()
                .antMatchers("/api/security/reset-password/*").permitAll()
                // Disallow everything else..
                .antMatchers("/api/user/*").authenticated();
        //Apply JWT
        http.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtExceptionHandlerFilter, JwtAuthorizationFilter.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
