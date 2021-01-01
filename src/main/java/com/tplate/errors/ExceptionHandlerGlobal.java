package com.tplate.errors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tplate.util.JsonUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;

@ControllerAdvice
@Controller
public class ExceptionHandlerGlobal {
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity handleBusinessValidationException(HttpMessageNotReadableException exception) throws JsonProcessingException {
        ErrorResponse errorResponse = new ErrorResponse(exception);
        return   new ResponseEntity<>(JsonUtil.convertObjectToJson(errorResponse), HttpStatus.CONFLICT);
    }
}
