/*
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.knowledge.core.transforms.impl;

import com.ghasemkiani.util.icu.PersianCalendar;
import ir.ac.iust.dml.kg.knowledge.core.TypedValue;
import ir.ac.iust.dml.kg.knowledge.core.ValueType;
import ir.ac.iust.dml.kg.knowledge.core.transforms.ITransformer;
import ir.ac.iust.dml.kg.knowledge.core.transforms.TransformException;
import ir.ac.iust.dml.kg.knowledge.core.transforms.Transformer;
import org.joda.time.DateTime;
import org.joda.time.chrono.IslamicChronology;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Transformer(value = "gregorianDate", description = "تبدیل هر رشته تاریخی به تاریخ")
public class CommandoDateTransformer implements ITransformer {

  private int getMonth(String monthName) {
    switch (monthName) {
      case "january":
      case "jan":
      case "ژانویه":
        return 1;
      case "february":
      case "feb":
      case "فوریه":
        return 2;
      case "march":
      case "mar":
      case "مارس":
      case "مارچ":
        return 3;
      case "april":
      case "apr":
      case "آوریل":
      case "آپریل":
      case "اپریل":
        return 4;
      case "may":
      case "می":
      case "مه":
        return 5;
      case "june":
      case "jun":
      case "جون":
      case "ژوئن":
      case "ژون":
        return 6;
      case "july":
      case "jul":
      case "جولای":
      case "ژولای":
        return 7;
      case "august":
      case "aug":
      case "اوت":
      case "آگوست":
      case "آگست":
        return 8;
      case "september":
      case "sep":
      case "سپتامبر":
        return 9;
      case "october":
      case "oct":
      case "اکتبر":
        return 10;
      case "november":
      case "nov":
      case "نوامبر":
        return 11;
      case "december":
      case "dec":
      case "دسامبر":
        return 12;
      case "فروردین":
        return 1;
      case "اردیبهشت":
      case "اردی‌بهشت":
      case "اردی بهشت":
        return 2;
      case "خرداد":
        return 3;
      case "تیر":
        return 4;
      case "مرداد":
      case "امرداد":
        return 5;
      case "شهریور":
        return 6;
      case "مهر":
        return 7;
      case "آبان":
        return 8;
      case "آذر":
        return 9;
      case "دی":
        return 10;
      case "" +
          "بهمن":
        return 11;
      case "اسفند":
        return 12;
      case "محرم":
      case "محرم‌الحرام":
      case "محرم الحرام":
        return 1;
      case "صفر":
        return 2;
      case "ربیع‌الاول":
      case "ربیع الاول":
        return 3;
      case "ربیع‌الثانی":
      case "ربیع الثانی":
        return 4;
      case "جمادی‌الاول":
      case "جمادی الاول":
        return 5;
      case "جمادی‌الثانی":
      case "جمادی الثانی":
        return 6;
      case "رجب":
        return 7;
      case "شعبان":
        return 8;
      case "رمضان":
        return 9;
      case "شوال":
        return 10;
      case "ذی‌قعده":
      case "ذیقعده":
      case "ذی قعده":
        return 11;
      case "ذی‌حجه":
      case "ذیحجه":
      case "ذی حجه":
        return 12;
    }
    return 0;
  }

  private final static String gregorianRegex = "(?U)(\\d+)\\s+" +
      "(january|jan|february|feb|march|mar" +
      "|april|apr|may|june|jun|july|jul|august|aug|september|sep|october|oct|november|nov|december|dec)" +
      "\\s+(\\d+).*";
  private final static String gregorianRegex2 = "(?U)(\\d+)\\s+" +
      "(ژانویه|فوریه|مارس|مارچ|آوریل|آپریل" +
      "|اپریل|می|مه|جون|ژوئن|ژون|جولای|ژولای|اوت|آگوست|آگست|سپتامبر|اکتبر|نوامبر|دسامبر)" +
      "\\s+(\\d+).*";
  private final static String jalaliRegex = "(?U)(\\d+)\\s+" +
      "(امرداد|مرداد|تیر|خرداد|اردیبهشت|اردی‌بهشت|اردی بهشت|فروردین" +
      "|شهریور|مهر|آبان|آذر|دی|بهمن|اسفند)" +
      "\\s*(ماه\\s+)*(\\d+).*";
  private final static String lunarRegex = "(?U)(\\d+)(ماه\\s+)*\\s+" +
      "(ربیع‌الثانی|ربیع الاول|ربیع‌الاول|صفر|محرم الحرام|محرم‌الحرام|محرم" +
      "|ربیع الثانی|جمادی الاول|جمادی‌الاول|جمادی‌الثانی|جمادی الثانی|رجب|شعبان|رمضان|شوال|ذی‌قعده|ذیقعده|ذی قعده" +
      "|ذی‌حجه|ذی حجه|ذیحجه)" +
      "\\s+(\\d+).*";
  private final static String gMonthRegex = "(?U)" +
      "(ژانویه|فوریه|مارس|مارچ|آوریل|آپریل" +
      "|اپریل|می|مه|جون|ژوئن|ژون|جولای|ژولای|اوت|آگوست|آگست|سپتامبر|اکتبر|نوامبر|دسامبر)" +
      "\\s+(\\d+).*";
  private final static String jMonthRegex = "(?U)" +
      "(امرداد|مرداد|تیر|خرداد|اردیبهشت|فروردین" +
      "|شهریور|مهر|آبان|آذر|دی|بهمن|اسفند)" +
      "\\s+(\\d+).*";
  private final static String lMonthRegex = "(?U)" +
      "(ربیع‌الثانی|ربیع الاول|ربیع‌الاول|محرم الحرام|محرم‌الحرام|محرم" +
      "|ربیع الثانی|جمادی الاول|جمادی‌الاول|جمادی‌الثانی|جمادی الثانی|رجب|شعبان|رمضان|شوال|ذی‌قعده|ذیقعده|ذی قعده" +
      "|ذی‌حجه|ذی حجه|ذیحجه)" +
      "\\s+(\\d+).*";
  private final static String yearRegex = "(?U)(\\d+)\\s*(هجری قمری|هجری شمسی|هجری خورشیدی|قمری|شمسی|خورشیدی|میلادی)*.*";
  private final static Pattern GRE_PATTERN = Pattern.compile(gregorianRegex);
  private final static Pattern GRE_PATTERN2 = Pattern.compile(gregorianRegex2);
  private final static Pattern JAL_PATTERN = Pattern.compile(jalaliRegex);
  private final static Pattern LUNAR_PATTERN = Pattern.compile(lunarRegex);
  private final static Pattern GMONTH_PATTERN = Pattern.compile(gMonthRegex);
  private final static Pattern JMONTH_PATTERN = Pattern.compile(jMonthRegex);
  private final static Pattern LMONTH_PATTERN = Pattern.compile(lMonthRegex);
  private final static Pattern YEAR_PATTERN = Pattern.compile(yearRegex);

  @Override
  public TypedValue transform(String value, String lang, ValueType type, String unit) throws TransformException {
    try {
      value = value.toLowerCase();
      Matcher matcher = GRE_PATTERN.matcher(value);
      if (matcher.matches()) return extractGre(matcher);
      matcher = GRE_PATTERN2.matcher(value);
      if (matcher.matches()) return extractGre(matcher);
      matcher = JAL_PATTERN.matcher(value);
      if (matcher.matches()) return extractJal(matcher);
      matcher = LUNAR_PATTERN.matcher(value);
      if (matcher.matches()) return extractLun(matcher);
      matcher = GMONTH_PATTERN.matcher(value);
      if (matcher.matches()) return extractGreMonth(matcher);
      matcher = JMONTH_PATTERN.matcher(value);
      if (matcher.matches()) return extractJalMonth(matcher);
      matcher = LMONTH_PATTERN.matcher(value);
      if (matcher.matches()) return extractLunMonth(matcher);
      matcher = YEAR_PATTERN.matcher(value);
      if (matcher.matches()) return extractYEAR(matcher);
      throw new TransformException();
    } catch (Throwable th) {
      throw new TransformException(th);
    }
  }

  private TypedValue extractYEAR(Matcher matcher) {
    int year = Integer.parseInt(matcher.group(1));
    if (matcher.group(2) == null) {
      if (year > 1410)
//        return new TypedValue(ValueType.Integer, String.valueOf(getGregorianDate(year, 1, 1)));
        return new TypedValue(ValueType.String, year  + " میلادی");
      else
//        return new TypedValue(ValueType.Date, String.valueOf(getJalaliDate(year, 1, 1)));
        return new TypedValue(ValueType.String, String.valueOf(year));
    }
    switch (matcher.group(2)) {
      case "میلادی":
//        return new TypedValue(ValueType.Date, String.valueOf(getGregorianDate(year, 1, 1)));
        return new TypedValue(ValueType.String, year + " میلادی");
      case "خورشیدی":
      case "شمسی":
      case "هجری خورشیدی":
      case "هجری شمسی":
//        return new TypedValue(ValueType.Date, String.valueOf(getJalaliDate(year, 1, 1)));
        return new TypedValue(ValueType.String, year + " خورشیدی");
      case "قمری":
      case "هجری قمری":
//        return new TypedValue(ValueType.Date, String.valueOf(getLunarDate(year, 1, 1)));
        return new TypedValue(ValueType.String, year + " قمری");
    }
    return null;
  }

  private TypedValue extractLunMonth(Matcher matcher) {
    final String monthName = matcher.group(1);
    int month = getMonth(monthName);
    int year = Integer.parseInt(matcher.group(3));
    return new TypedValue(ValueType.Date, String.valueOf(getLunarDate(year, month, 1)));
  }

  private TypedValue extractLun(Matcher matcher) {
    final int day = Integer.parseInt(matcher.group(1));
    final String monthName = matcher.group(3);
    int month = getMonth(monthName);
    int year = Integer.parseInt(matcher.group(4));
    return new TypedValue(ValueType.Date, String.valueOf(getLunarDate(year, month, day)));
  }

  private long getLunarDate(int year, int month, int day) {
    return new DateTime(year, month, day + 1, 0, 0, 0, 0, IslamicChronology.getInstance()).getMillis();
  }

  private TypedValue extractJalMonth(Matcher matcher) {
    final String monthName = matcher.group(1);
    int month = getMonth(monthName);
    int year = Integer.parseInt(matcher.group(2));
//    if (year < 100) year += 1300;
    return new TypedValue(ValueType.Date, String.valueOf(getJalaliDate(year, month, 1)));
  }

  private TypedValue extractJal(Matcher matcher) {
    final int day = Integer.parseInt(matcher.group(1));
    final String monthName = matcher.group(2);
    int month = getMonth(monthName);
    int year = Integer.parseInt(matcher.group(4));
//    if (year < 100) year += 1300;
    return new TypedValue(ValueType.Date, String.valueOf(getJalaliDate(year, month, day)));
  }

  private Long getJalaliDate(int year, int month, int day) {
    PersianCalendar calendar = new PersianCalendar(year, month - 1, day, 0, 0, 0);
    return calendar.getTimeInMillis();
  }

  private TypedValue extractGre(Matcher matcher) {
    final int day = Integer.parseInt(matcher.group(1));
    final String monthName = matcher.group(2);
    int month = getMonth(monthName);
    int year = Integer.parseInt(matcher.group(3));
//    if (year < 18) year += 2000;
//    else if (year < 100) year += 1900;
    return new TypedValue(ValueType.Date, String.valueOf(getGregorianDate(year, month, day)));
  }

  private TypedValue extractGreMonth(Matcher matcher) {
    final String monthName = matcher.group(1);
    int month = getMonth(monthName);
    int year = Integer.parseInt(matcher.group(2));
//    if (year < 18) year += 2000;
//    else if (year < 100) year += 1900;
    return new TypedValue(ValueType.Date, String.valueOf(getGregorianDate(year, month, 1)));
  }

  private Long getGregorianDate(int year, int month, int day) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month - 1);
    calendar.set(Calendar.DAY_OF_MONTH, day);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTimeInMillis();
  }
}
