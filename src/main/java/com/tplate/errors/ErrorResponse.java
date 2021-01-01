package com.tplate.errors;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.Data;
import org.springframework.http.converter.HttpMessageNotReadableException;

@Data
public class ErrorResponse {
    private String code;
    private String message;

    public ErrorResponse(Exception e) {
        this.code = "500";
        this.message = "Error interno de servidor";
    }

    public ErrorResponse(ExpiredJwtException e) {
        this.code = "401";
        this.message = "Sesion finalizada";
    }
    public ErrorResponse(HttpMessageNotReadableException e) {
        this.code = "409";
        String msg = e.getMessage();
        this.message =
                msg.contains(":") ? msg.substring(0, msg.indexOf(":")) : msg;
    }
}
