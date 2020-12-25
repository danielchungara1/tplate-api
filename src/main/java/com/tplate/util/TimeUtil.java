package com.tplate.util;

import com.tplate.constans.TimeConstants;

public class TimeUtil {
    public static Long toMilseconds(Hora hora){
        return 1L * hora.getValue() * TimeConstants.MINUTOS_EN_HORA * TimeConstants.SEGUNDOS_EN_MINUTO  * TimeConstants.MILLISEGUNDOS_EN_SEGUNDO;
    }
}
