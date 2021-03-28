package com.tplate.handlers;

import com.tplate.builders.ResponseEntityBuilder;
import com.tplate.util.JsonUtil;
import com.tplate.util.StringUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Log4j2
@Component
public class JwtExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e){
            log.error("JWT exception. {}", e.getClass().getCanonicalName());

            response.setContentType("application/json");
            response.setStatus(HttpStatus.BAD_REQUEST.value());

            ResponseEntity r = ResponseEntityBuilder.buildBadRequest("Session has finished.");
            response.getWriter().write(JsonUtil.convertObjectToJson(r.getBody()));

        } catch (Exception e){
            log.error("JWT exception. {}", e.getClass().getCanonicalName());

            response.setContentType("application/json");
            response.setStatus(HttpStatus.BAD_REQUEST.value());

            ResponseEntity r = ResponseEntityBuilder.buildBadRequest(StringUtil.truncate(e.getMessage(), ":"));
            response.getWriter().write(JsonUtil.convertObjectToJson(r.getBody()));

        }
    }
}