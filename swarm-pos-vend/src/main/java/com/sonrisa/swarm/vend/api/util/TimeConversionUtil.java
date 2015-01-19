package com.sonrisa.swarm.vend.api.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeConversionUtil {

    public static Timestamp stringToDate(String date) throws ParseException{
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    	Date parsedDate = dateFormat.parse(date);
        return new java.sql.Timestamp(parsedDate.getTime());
    }
}
