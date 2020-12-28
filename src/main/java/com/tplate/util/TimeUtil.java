package com.tplate.util;

import com.tplate.constans.Time;

public class TimeUtil {
    public static Long toMilseconds(Hora hora){
        return 1L * hora.getValue() * Time.MINUTOS_EN_HORA * Time.SEGUNDOS_EN_MINUTO  * Time.MILLISEGUNDOS_EN_SEGUNDO;
    }
}
