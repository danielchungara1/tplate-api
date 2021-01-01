package com.tplate.errors;

import com.tplate.errors.ErrorReturn;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class) // used to define the exception type for the function is Exception type
    @ResponseBody
    public ErrorReturn<Object> handlerException(Exception e)throws Exception{
        ErrorReturn<Object> r = new ErrorReturn<>();
        r.setCode("500");
        r.setData( "Error interno de servidor." );
        r.setMessage(e.getMessage());
        return r;
    }

}