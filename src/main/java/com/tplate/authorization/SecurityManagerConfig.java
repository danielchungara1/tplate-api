package com.tplate.authorization;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@EnableWebSecurity
public class SecurityManagerConfig extends WebSecurityConfigurerAdapter {

//    @Autowired
//    UserRepository userRepository;

//    @Autowired
//    JwtTokenFilter jwtTokenFilter;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        super.configure(auth);
//        auth.userDetailsService( username -> userRepository
//        .findByUsername(username)
//        .orElseThrow(
//                () -> new UsernameNotFoundException(format("User: %s, not found.", username))
//        ));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //Enable cross origin all controllers
        http = http.cors().and().csrf().disable();
        //Sessions are management by token
        http = http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and();
        //Public and private Paths
        http.authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "*").permitAll()
                .antMatchers("/api/login").permitAll()
                .anyRequest().authenticated();

    }

    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
