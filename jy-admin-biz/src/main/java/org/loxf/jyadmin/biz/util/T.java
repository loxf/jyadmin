package org.loxf.jyadmin.biz.util;

import java.math.BigDecimal;

public class T {
    public static void main(String [] args){
        int min = 1, max = 9999;
        for(int i=0;i<500;i++) {
            int randNum = min + (int)(Math.random() * ((max - min) + 1));
            String code = randNum + "";
            boolean printflag = false;
            if(code.length()<4){
                printflag = true;
                for(int len=code.length(); len<4; len++ ){
                    code = "0" + code;
                }
            }
            if(printflag)
                System.out.println(code);
        }
    }


}
