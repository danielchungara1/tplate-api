package com.tplate.responses.builders;

import com.tplate.responses.dtos.ResponseDto;
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
                .statusCode__conflict()
                .message(message)
                .build();
    }

    public static ResponseEntity buildBadRequest(String message) {
        return ResponseEntityBuilder
                .builder()
                .statusCode__badRequest()
                .message(message)
                .build();
    }

    public static ResponseEntity buildOk(String message) {
        return ResponseEntityBuilder
                .builder()
                .statusCode__ok()
                .message(message)
                .build();
    }

    public static ResponseEntity buildNotFound(String message) {
        return ResponseEntityBuilder
                .builder()
                .statusCode__notFound()
                .message(message)
                .build();
    }

//    **************************************************************************
//    * Shortcuts Status Codes
//    **************************************************************************

    public ResponseEntityBuilder statusCode__badRequest() {
        this.statusCode = HttpStatus.BAD_REQUEST;
        return this;
    }

    public ResponseEntityBuilder statusCode__ok() {
        this.statusCode = HttpStatus.OK;
        return this;
    }

    public ResponseEntityBuilder statusCode__conflict() {
        this.statusCode = HttpStatus.CONFLICT;
        return this;
    }

    public ResponseEntityBuilder statusCode__notFound() {
        this.statusCode = HttpStatus.NOT_FOUND;
        return this;
    }




}
