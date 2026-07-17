package com.spt.bas.client.util;

import cn.hutool.core.convert.Convert;

/**
 * @Author MoonLight
 * @Date 2023/3/10 9:46
 * @Version 1.0
 */
public class RmbUtil {

    public static String number2Chinese(Number num) {
        String chinese = Convert.digitToChinese(num);
        if (chinese.endsWith("角")) {
            chinese = chinese + "整";
        }
        return chinese;
    }

//    public static void main(String[] args) {
//        BigDecimal value = new BigDecimal("122574.38");
//        BigDecimal value1 = new BigDecimal("186167.50");
//        BigDecimal value2 = new BigDecimal("101040.80");
//
//        System.out.println(Convert.digitToChinese(value));
//        System.out.println(RmbConvert.number2CN(value));
//        System.out.println(RmbUtil.number2Chinese(value));
//
//        System.out.println(Convert.digitToChinese(value1));
//        System.out.println(RmbConvert.number2CN(value1));
//        System.out.println(RmbUtil.number2Chinese(value1));
//
//        System.out.println(Convert.digitToChinese(value2));
//        System.out.println(RmbConvert.number2CN(value2));
//        System.out.println(RmbUtil.number2Chinese(value2));
//    }
}
