package org.loxf.jyadmin.biz.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.loxf.jyadmin.base.util.DateUtils;

import java.math.BigDecimal;
import java.util.*;

public class BizUtil {
    public static void main(String [] args){
    }

    public static String[][] getArrData(List<Map> data){
        String [][] days = new String[data.size()][2];
        int i=0;
        for(Map map : data) {
            String [] tmp = new String[2];
            tmp[0] = map.get("name").toString();
            tmp[1] = map.get("value").toString();
            days[i++]=tmp;
        }
        return days;
    }

    public static String[][] getDataByDate(List<Map> data){
        String [][] days = new String[7][];
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -6);
        for(int i=0; i<7; i++){
            String[] value = new String[2];
            value[0] = DateUtils.format(calendar.getTime());
            if(CollectionUtils.isNotEmpty(data)){
                String tmp = "0";
                for(Map map :data){
                    if(value[0].equals(map.get("name"))){
                        tmp = map.get("value").toString();
                        break;
                    }
                }
                value[1] = tmp;
            } else {
                value[1] = "0";
            }
            calendar.add(Calendar.DATE, 1);
            days[i] = value;
        }
        return days;
    }
}
