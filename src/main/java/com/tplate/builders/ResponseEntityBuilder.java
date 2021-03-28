package com.tplate.builders;

import com.tplate.dtos.ResponseDto;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class  ResponseEntityBuilder {

    private String mesage = "";
    private Object dto;
    private HttpStatus statusCode = HttpStatus.OK;

    private static final ModelMapper modelMapper = new ModelMapper();

    private ResponseEntityBuilder(){}

    public static ResponseEntityBuilder builder() {
        return new ResponseEntityBuilder();
    }

    public ResponseEntityBuilder message(String message) {
        this.mesage = message;
        return this;
    }

    public ResponseEntityBuilder dto(Object entity, Class dtoClass) {
        this.dto = modelMapper.map(entity, dtoClass);
        return this;
    }

    public ResponseEntityBuilder statusCode(HttpStatus httpStatus) {
        this.statusCode = httpStatus;
        return this;
    }

    public ResponseEntity build() {
        ResponseDto responseDto = ResponseDto.builder()
                .message(this.mesage)
                .data(this.dto)
                .build();
        return new ResponseEntity(responseDto, this.statusCode);
    }

//    **************************************************************************
//    * Shortcuts Responses: Only message and status code.
//    **************************************************************************

    public static ResponseEntity buildConflict(String message) {
        return ResponseEntityBuilder
                .builder()
                .conflict()
                .message(message)
                .build();
    }

    public static ResponseEntity buildBadRequest(String message) {
        return ResponseEntityBuilder
                .builder()
                .badRequest()
                .message(message)
                .build();
    }

    public static ResponseEntity buildOk(String message) {
        return ResponseEntityBuilder
                .builder()
                .ok()
                .message(message)
                .build();
    }

    public static ResponseEntity buildNotFound(String message) {
        return ResponseEntityBuilder
                .builder()
                .notFound()
                .message(message)
                .build();
    }

    public static ResponseEntity buildInternalServerError(String message) {
        return ResponseEntityBuilder
                .builder()
                .internalServerError()
                .message(message)
                .build();
    }

    public static ResponseEntity buildForbidden(String message) {
        return ResponseEntityBuilder
                .builder()
                .forbidden()
                .message(message)
                .build();
    }

    public static ResponseEntity buildSomethingWrong() {
        return ResponseEntityBuilder
                .builder()
                .internalServerError()
                .message("Something went wrong.")
                .build();
    }

    public static ResponseEntity buildSomethingWrong(String details) {
        return ResponseEntityBuilder
                .builder()
                .internalServerError()
                .message("Something went wrong. " + details)
                .build();
    }


//    **************************************************************************
//    * Shortcuts Status Code
//    **************************************************************************

    public ResponseEntityBuilder badRequest() {
        this.statusCode = HttpStatus.BAD_REQUEST;
        return this;
    }

    public ResponseEntityBuilder ok() {
        this.statusCode = HttpStatus.OK;
        return this;
    }

    public ResponseEntityBuilder conflict() {
        this.statusCode = HttpStatus.CONFLICT;
        return this;
    }

    public ResponseEntityBuilder notFound() {
        this.statusCode = HttpStatus.NOT_FOUND;
        return this;
    }

    public ResponseEntityBuilder internalServerError() {
        this.statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        return this;
    }

    public ResponseEntityBuilder forbidden() {
        this.statusCode = HttpStatus.FORBIDDEN;
        return this;
    }


}
