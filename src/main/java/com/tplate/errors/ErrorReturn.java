package com.tplate.errors;

import lombok.Data;

@Data
public class ErrorReturn<T> {
    private String code;
    private String data;
    private T message;
}
