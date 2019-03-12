package com.example.wujia2.utils;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter  {

  public static Date stringToDate(String source) {

    if (!source.equals("")) {

      SimpleDateFormat format;
      if (source.length() == 10) {
        format = new SimpleDateFormat("yyyy-MM-dd");

      } else {
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
      }

      Date date = null;
      try {
        date = format.parse(source);
      } catch (ParseException e) {
        e.printStackTrace();
      }
      return date;
    }

    return null;
  }

  public static String dateToStr(java.util.Date dateDate) {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    String dateString = formatter.format(dateDate);
    return dateString;
  }
}
