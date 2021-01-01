package com.tplate.errors;

import com.tplate.util.JsonUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
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

    private ErrorResponse errorResponse;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        }catch (ExpiredJwtException e){
            errorResponse = new ErrorResponse(e);
            log.error("Token expiro. {}", e.getClass().getCanonicalName());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(JsonUtil.convertObjectToJson(errorResponse));
        }
        catch (Exception e) {
            // custom error response class used across my project
            errorResponse = new ErrorResponse(e);
            log.error("Error interno de servidor. {}", e.getClass().getCanonicalName());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.getWriter().write(JsonUtil.convertObjectToJson(errorResponse));
        }
    }
}