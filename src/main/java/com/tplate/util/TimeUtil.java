package com.tplate.util;

import com.tplate.constans.Time;

public class TimeUtil {
    public static Long toMiliseconds(Minutes minutes){
        return 1L * minutes.getValue()  * Time.SEGUNDOS_EN_MINUTO  * Time.MILLISEGUNDOS_EN_SEGUNDO;
    }
}
