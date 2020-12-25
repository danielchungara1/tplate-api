//package com.tplate.authorization;
//
//import com.google.common.base.Strings;
//import com.google.common.net.HttpHeaders;
//import com.tplate.user.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.Collections;
//
//@Component
//public class JwtTokenFilter extends OncePerRequestFilter {
//    @Autowired
//    JwtTokenUtil jwtTokenUtil;
//    @Autowired
//    UserRepository userRepository;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse
//            , FilterChain filterChain) throws ServletException, IOException {
//
//        String header = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
//        if (Strings.isNullOrEmpty(header) || !header.startsWith("Bearer ")){
//            filterChain.doFilter(httpServletRequest, httpServletResponse);
//            return;
//        }
//
//        String token = header.split(" ")[1].trim();
//        if (!jwtTokenUtil.validate(token)){
//            filterChain.doFilter(httpServletRequest, httpServletResponse);
//            return;
//        }
//
//        UserDetails userDetails = userRepository
//                .findByUsername(jwtTokenUtil.getUsername(token))
//                .orElse(null);
//        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                userDetails, null, userDetails == null ? Collections.emptyList() : userDetails.getAuthorities()
//        );
//        authentication.setDetails(
//                new WebAuthenticationDetailsSource().buildDetails(httpServletRequest)
//        );
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        filterChain.doFilter(httpServletRequest, httpServletResponse);
//
//    }
//}
