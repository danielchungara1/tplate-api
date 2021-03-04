package com.tplate.security.services;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Random;

@Service
@Log4j2
/* Todo: Se hizo un service en vez de una clase util con metodos estaticos porque
      mas adelamte se tomaran parametros del codigo de reset del archivo de configuracion
 */
public class ResetPasswordService {

     // Miscelaneos
    private Random rand = new Random();

    private final Integer EXPIRATION_MIN = 5;

    public String code (){
        return String.format("%04d", this.rand.nextInt(10000));
    }

    public Date expiration(){
        return  new Date(System.currentTimeMillis() + (this.EXPIRATION_MIN * 60 * 1000));
    }

}
