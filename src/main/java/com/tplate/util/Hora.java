package com.tplate.util;

import lombok.Data;

@Data
public class Hora {
    private Integer value; //-2.147.483.648 to 2.147.483.648

    public Hora(Integer value) {
        this.value = value;
    }
}
