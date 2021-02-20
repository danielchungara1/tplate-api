package com.tplate.responses.builders;

import com.tplate.responses.dtos.ResponseDto;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseBuilder {

    private String mesage = "";
    private Object dto;
    private HttpStatus statusCode = HttpStatus.OK;

    private static ModelMapper modelMapper = new ModelMapper();

    public static ResponseBuilder builder() {
        return new ResponseBuilder();
    }

    public static ResponseEntity buildConflict(String message) {
        return ResponseBuilder
                .builder()
                .conflict()
                .message(message)
                .build();
    }

    public ResponseBuilder message(String message) {
        this.mesage = message;
        return this;
    }

    public ResponseBuilder dto(Object entity, Class dtoClass) {
        this.dto = modelMapper.map(entity, dtoClass);
        return this;
    }

    public ResponseBuilder conflict() {
        this.statusCode = HttpStatus.CONFLICT;
        return this;
    }

    public ResponseBuilder ok() {
        this.statusCode = HttpStatus.OK;
        return this;
    }

    public ResponseEntity build() {
        ResponseDto responseDto = ResponseDto.builder()
                .message(this.mesage)
                .data(this.dto)
                .build();
        return new ResponseEntity(responseDto, this.statusCode);
    }

}
