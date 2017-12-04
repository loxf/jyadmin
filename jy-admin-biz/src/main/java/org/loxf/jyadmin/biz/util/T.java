package org.loxf.jyadmin.biz.util;

import org.loxf.jyadmin.base.util.DateUtils;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

public class T {
    public static void main(String [] args){
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.MINUTE, 15);
        System.out.println(DateUtils.format(now, "yyyyMMddHHmmss"));
        System.out.println(DateUtils.format(calendar.getTime(), "yyyyMMddHHmmss"));
    }


}
