package com.spt.bas.server.util;

import com.spt.tools.core.date.DateOperator;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @Author: shengong
 * @Date: Created in 2021-02-02 13:59
 */
@Slf4j
public class SptDateUtils {
    public static Date formatterDate(Date date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DateOperator.FORMAT_STR);
            if (date == null) {
                date = new Date();
            }
            String format = sdf.format(date);
            date = sdf.parse(format);
        } catch (ParseException e) {
            log.error("formatterDate error:{}", date);
        }
        return date;
    }
}
