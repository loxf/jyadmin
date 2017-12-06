package org.loxf.jyadmin.biz.util;

import org.loxf.jyadmin.base.util.DateUtils;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class T {
    public static void main(String [] args){
        System.out.println(DateUtils.format(new Date(), "yyMMddHHmmss") + RandomUtils.getRandomStr(4));
    }


}
