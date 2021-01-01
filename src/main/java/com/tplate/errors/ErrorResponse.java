package com.tplate.errors;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.Data;

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
}
