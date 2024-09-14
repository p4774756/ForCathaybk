package com.example.demo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Util {
    public static String convertFromQueryToDatabaseDateFormat(String inputDate) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");

        Date date = inputFormat.parse(inputDate);
        return outputFormat.format(date);
    }

    public static String convertFromWebToDatabaseDateFormat(String inputDate) throws ParseException {
        // Define the format of the original date string
        DateTimeFormatter originalFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        // Define the target format
        DateTimeFormatter targetFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDate date = LocalDate.parse(inputDate, originalFormatter);

        return date.atStartOfDay().format(targetFormatter);
    }
}
