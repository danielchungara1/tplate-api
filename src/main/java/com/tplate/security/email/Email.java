package com.tplate.security.email;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Email {
    private String to;
    private String token;
}
