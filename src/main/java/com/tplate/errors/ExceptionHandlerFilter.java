package com.tplate.errors;

import com.tplate.responses.builders.ResponseEntityBuilder;
import com.tplate.util.JsonUtil;
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
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        }catch (ExpiredJwtException e){
            log.error("Token has expired. {}", e.getClass().getCanonicalName());

            response.setContentType("application/json");
            response.setStatus(HttpStatus.BAD_REQUEST.value());

            ResponseEntity r = ResponseEntityBuilder.buildBadRequest("JWT has expired.");
            response.getWriter().write(JsonUtil.convertObjectToJson(r.getBody()));

        }
        catch (Exception e) {
            log.error("Internal Server Error. {}", e.getClass().getCanonicalName());

            response.setContentType("application/json");
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

            ResponseEntity r = ResponseEntityBuilder.buildInternalServerError("Internal Server Error.");
            response.getWriter().write(JsonUtil.convertObjectToJson(r.getBody()));

        }
    }
}