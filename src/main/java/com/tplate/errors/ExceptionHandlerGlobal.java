package com.tplate.errors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tplate.responses.builders.ResponseEntityBuilder;
import com.tplate.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
@Controller
@Slf4j
public class ExceptionHandlerGlobal {

    // Incompatible datatype between dto-controller and json-received
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity handleBusinessValidationException(HttpMessageNotReadableException exception) throws JsonProcessingException {
        log.error(exception.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(exception);
        return ResponseEntityBuilder.buildBadRequest("Check the data sent. " + errorResponse.getMessage());
    }

    // Not Found ( 404 )
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseBody
    public ResponseEntity handleNoHandlerFound(NoHandlerFoundException e, WebRequest request) {
        log.error(e.getMessage());
        return ResponseEntityBuilder.buildNotFound("Resource not found . " + e.getRequestURL());

    }
}
