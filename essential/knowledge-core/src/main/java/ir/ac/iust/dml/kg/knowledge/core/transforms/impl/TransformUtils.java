/*
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by Mohammad Abdous.
 */

package ir.ac.iust.dml.kg.knowledge.core.transforms.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.regex.Pattern;

public class TransformUtils {

  private static final HashMap<String, String> enMonthDic;
  private static final HashMap<String, String> shamsiMonthDic;

  static {
    shamsiMonthDic = new HashMap<String, String>();
    shamsiMonthDic.put("فروردین", "1");
    shamsiMonthDic.put("اردیبهشت", "2");
    shamsiMonthDic.put("خرداد", "3");
    shamsiMonthDic.put("تیر", "4");
    shamsiMonthDic.put("مرداد", "5");
    shamsiMonthDic.put("شهریور", "6");
    shamsiMonthDic.put("مهر", "7");
    shamsiMonthDic.put("آبان", "8");
    shamsiMonthDic.put("آذر", "9");
    shamsiMonthDic.put("دی", "10");
    shamsiMonthDic.put("بهمن", "11");
    shamsiMonthDic.put("اسفند", "12");
  }

  static {
    enMonthDic = new HashMap<>();
    enMonthDic.put("ژانویه", "1");
    enMonthDic.put("فوریه", "2");
    enMonthDic.put("مارس", "3");
    enMonthDic.put("آوریل", "4");
    enMonthDic.put("مه", "5");
    enMonthDic.put("ژوئن", "6");
    enMonthDic.put("ژوئیه", "7");
    enMonthDic.put("اوت", "8");
    enMonthDic.put("سپتامبر", "9");
    enMonthDic.put("اکتبر", "10");
    enMonthDic.put("نوامبر", "11");
    enMonthDic.put("دسامبر", "12");

    enMonthDic.put("january", "1");
    enMonthDic.put("february", "2");
    enMonthDic.put("march", "3");
    enMonthDic.put("april", "4");
    enMonthDic.put("may", "5");
    enMonthDic.put("june", "6");
    enMonthDic.put("july", "7");
    enMonthDic.put("august", "8");
    enMonthDic.put("september", "9");
    enMonthDic.put("october", "10");
    enMonthDic.put("november", "11");
    enMonthDic.put("december", "12");
  }

  public static String convertToEnglishDigits(String value) {

    return value.replace("١", "1").replace("٢", "2").replace("٣", "3").replace("٤", "4").replace("٥", "5")
        .replace("٦", "6").replace("7", "٧").replace("٨", "8").replace("٩", "9").replace("٠", "0")
        .replace("۱", "1").replace("۲", "2").replace("۳", "3").replace("۴", "4").replace("۵", "5")
        .replace("۶", "6").replace("۷", "7").replace("۸", "8").replace("۹", "9").replace("۰", "0");
  }

  public static String detectDateType(String value) {
    for (String key : enMonthDic.keySet())
      if (value.toLowerCase().contains(key))
        return "miladi";
    for (String key : shamsiMonthDic.keySet())
      if (value.toLowerCase().contains(key))
        return "shamsi";
    return "miladi";
  }

  @Deprecated
  public static Date miladiTransformer(String value) {
    return getDate(value, enMonthDic);
  }

  @Deprecated
  public static Date shamsiTransformer(String value) {
    return getDate(value, shamsiMonthDic);
  }

  @Deprecated
  private static Date getDate(String value, HashMap<String, String> monthDic) {
    value = value.toLowerCase();

    int year = 0;
    int month = 0;
    int day = 0;
    for (String key : monthDic.keySet()) {
      if (value.contains(key)) {
        month = Integer.parseInt(monthDic.get(key));
        String[] strs = value.split(key);
        year = Integer.parseInt(strs[1].trim());
        day = Integer.parseInt(strs[0].trim());
        break;
      }
    }
    Calendar calendar = new GregorianCalendar(year, month, day);
    return calendar.getTime();
  }

  private final static String rangeRegex = "(از|بین|مابین)*" +
      "\\s*(.*)\\s+" +
      "(تا|-|–|و|الی)" +
      "\\s+(.*)\\s*(سانتی‌گراد|متر|کلیومتر)*";
  final static Pattern RANGE_PATTERN = Pattern.compile(rangeRegex);
}
