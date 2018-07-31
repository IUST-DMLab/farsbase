/**/

var DAY_OF_WEEK = ['یکشنبه', 'دوشنبه', 'سه شنبه', 'چهارشنبه', 'پنجشنبه', 'جمعه', 'شنبه'];
var DAY_OF_WEEK_BRIEF = ['ی', 'د', 'س', 'چ', 'پ', 'ج', 'ش'];
var PERSIAN_MONTH = ['فروردین', 'اردیبهشت', 'خرداد', 'تیر', 'مرداد', 'شهریور', 'مهر', 'آبان', 'آذر', 'دی', 'بهمن', 'اسفند'];
var PERSIAN_MONTH_BRIEF = ['فر', 'ار', 'خر', 'تی', 'مر', 'شه', 'مه', 'آب', 'آذ', 'دی', 'به', 'اس'];
var PERSIAN_SEASON = ['بهار', 'تابستان', 'پاییز', 'زمستان'];

var g_days = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
var j_days = [31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29];
Date.gregorianToJalali = function (g_y, g_m, g_d) {
    g_y = parseInt(g_y);
    g_m = parseInt(g_m);
    g_d = parseInt(g_d);
    var gy = g_y - 1600;
    var gm = g_m - 1;
    var gd = g_d - 1;
    var g_day_no = 365 * gy + parseInt((gy + 3) / 4) - parseInt((gy + 99) / 100) + parseInt((gy + 399) / 400);
    for (var i = 0; i < gm; ++i)
        g_day_no += g_days[i];
    if (gm > 1 && ((gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0)))
        ++g_day_no;
    g_day_no += gd;
    var j_day_no = g_day_no - 79;
    var j_np = parseInt(j_day_no / 12053);
    j_day_no %= 12053;
    var jy = 979 + 33 * j_np + 4 * parseInt(j_day_no / 1461);
    j_day_no %= 1461;
    if (j_day_no >= 366) {
        jy += parseInt((j_day_no - 1) / 365);
        j_day_no = (j_day_no - 1) % 365;
    }
    for (var i = 0; i < 11 && j_day_no >= j_days[i]; ++i)
        j_day_no -= j_days[i];
    var jm = i + 1;
    var jd = j_day_no + 1;
    return [jy, jm, jd];
};

// TODO : check output format
// does month and day zero base !!! ????
Date.jalaliToGregorian = function (j_y, j_m, j_d) {
    if (arguments.length == 1) {
        var arr = j_y.split('/');
        j_y = arr[0];
        j_m = arr[1];
        j_d = arr[2];
    }

    var JalaliDate = {
        g_days_in_month: [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31],
        j_days_in_month: [31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29]
    };
    j_y = parseInt(j_y);
    j_m = parseInt(j_m);
    j_d = parseInt(j_d);
    var jy = j_y - 979;
    var jm = j_m - 1;
    var jd = j_d - 1;

    var j_day_no = 365 * jy + parseInt(jy / 33) * 8 + parseInt((jy % 33 + 3) / 4);
    for (var i = 0; i < jm; ++i) j_day_no += JalaliDate.j_days_in_month[i];

    j_day_no += jd;

    var g_day_no = j_day_no + 79;

    var gy = 1600 + 400 * parseInt(g_day_no / 146097);
    /* 146097 = 365*400 + 400/4 - 400/100 + 400/400 */
    g_day_no = g_day_no % 146097;

    var leap = true;
    if (g_day_no >= 36525) /* 36525 = 365*100 + 100/4 */
    {
        g_day_no--;
        gy += 100 * parseInt(g_day_no / 36524);
        /* 36524 = 365*100 + 100/4 - 100/100 */
        g_day_no = g_day_no % 36524;

        if (g_day_no >= 365)
            g_day_no++;
        else
            leap = false;
    }

    gy += 4 * parseInt(g_day_no / 1461);
    /* 1461 = 365*4 + 4/4 */
    g_day_no %= 1461;

    if (g_day_no >= 366) {
        leap = false;

        g_day_no--;
        gy += parseInt(g_day_no / 365);
        g_day_no = g_day_no % 365;
    }

    for (var i = 0; g_day_no >= JalaliDate.g_days_in_month[i] + (i == 1 && leap); i++)
        g_day_no -= JalaliDate.g_days_in_month[i] + (i == 1 && leap);
    var gm = i;
    var gd = g_day_no;

    return [gy, gm, gd];
};

Date.getJalaliDate = function (date, brief) {
    if (Object.prototype.toString.call(date) !== '[object Date]')
        date = new Date(date);

    var gy = date.getFullYear();
    var gm = date.getMonth() + 1;
    var gd = date.getDate();
    var d = Date.gregorianToJalali(gy, gm, gd);
    if (brief)
        return '{0} {1}'.format(d[2], PERSIAN_MONTH_NAMES[d[1] - 1]);
    return '{0} {1} {2}'.format(d[2], PERSIAN_MONTH_NAMES[d[1] - 1], d[0]);
};

Date.getJalaliDate2 = function (myDate, reverse) {
    //console.log('date in getJalaliDate2 :'+myDate)
    if (Object.prototype.toString.call(myDate) !== '[object Date]')
        myDate = new Date(myDate);

    var gy = myDate.getFullYear();
    var gm = myDate.getMonth() + 1;
    var gd = myDate.getDate();
    //console.log('date for convert  :'+'{2}/{1}/{0}'.format(gy, gm, gd));
    var d = Date.gregorianToJalali(gy, gm, gd);

    if (reverse)
        return '{2}/{1}/{0}'.format(d[0], d[1], d[2]);
    else
        return '{2}/{1}/{0}'.format(d[2], d[1], d[0]);
};

Date.getJalaliDate3 = function (date) {
    if (Object.prototype.toString.call(date) !== '[object Date]')
        date = new Date(date);

    var gy = date.getFullYear();
    var gm = date.getMonth() + 1;
    var gd = date.getDate();
    var d = Date.gregorianToJalali(gy, gm, gd);
    var dw = (date.getDay() + 1) % 7 + 1;

    var dy = d[2];

    for (var i = 1; i < d[1]; i++) {
        dy += j_days[i - 1];
    }

    var ds = d[2];
    for (var i = parseInt((d[1] - 1) / 3) * 3 + 1; i < d[1]; i++) {
        ds += j_days[i - 1];
    }

    return {
        year: d[0], month: d[1], day: d[2], dayOfWeek: dw, dayInSeason: ds, dayInYear: dy,
        monthName: PERSIAN_MONTH[d[1] - 1], monthNameBrief: PERSIAN_MONTH_BRIEF[d[1] - 1],
        seasonName: PERSIAN_SEASON[(d[1] - 1) / 3]
    };
};

Date.getJalaliDateTime = function (date, splitter) {
    if (Object.prototype.toString.call(date) !== '[object Date]')
        date = new Date(date);

    var p1 = Date.getJalaliDate(date);
    return p1 + (splitter ? splitter : ' - ') + '{0}:{1}:{2}'.format(date.getHours().lpad(2), date.getMinutes().lpad(2), date.getSeconds().lpad(2));
};

Date.getJalaliShortDateTime = function (date, splitter) {
    if (Object.prototype.toString.call(date) !== '[object Date]')
        date = new Date(date);

    var p1 = Date.getJalaliDate(date, true);
    return p1 + (splitter ? splitter : ' - ') + '{0}:{1}'.format(date.getHours().lpad(2), date.getMinutes().lpad(2));
};

Date.getJalaliTime = function (date) {
    if (Object.prototype.toString.call(date) !== '[object Date]')
        date = new Date(date);

    return '{0}:{1}'.format(date.getHours(), date.getMinutes().lpad(2));
};

Date.convertDateToString = function (date) {
    var day = date.getDate();
    var month = date.getMonth();
    var year = date.getFullYear();

    month = parseInt(month) + 1;

    if (day < 10) day = '0' + day;
    if (month < 10) month = '0' + month;

    date = year + '/' + month + '/' + day;
    return date;
};

Date.getDateDiffAsMilliSeconds = function (startDate, endDate) {
    return (endDate.getTime() - startDate.getTime());
};

Date.getDateDiffAsSeconds = function (startDate, endDate) {

    if (!endDate || !startDate) {
        return 0;
    }

    return (endDate.getTime() - startDate.getTime()) / 1000;
};

Date.getDateDiffAsDays = function (startDate, endDate) {
    return Date.getDateDiffAsSeconds(startDate, endDate) / 86400;
};

Date.getFirstOfJalaliMonths = function (startDate, endDate) {
    var datesArray = [];
    for (var i = Date.getJalaliDate3(startDate).year; i <= Date.getJalaliDate3(endDate).year; i++) {
        for (var j = 1; j <= 12; j++) {
            var gregorianDate = Date.jalaliToGregorian(i, j, 1);
            datesArray.push(new Date(gregorianDate[0], gregorianDate[1], gregorianDate[2] + 1));
        }
    }
    return datesArray;
};

Date.getFirstOfJalaliWeeks = function (startDate, endDate) {
    var k = 0,
        datesArray = [],
        dayOfMonth = 0;

    var firstSaturday = Date.findDateSaturdayInJalali(startDate);

    var startMonth = Date.getJalaliDate3(firstSaturday).month,
        endMonth = 12;


    for (var i = Date.getJalaliDate3(startDate).year; i <= Date.getJalaliDate3(endDate).year; i++) {
        for (var j = startMonth; j <= endMonth; j++) {
            for (var s = 0; s < 5; s += 1 , k += 7) {

//                if(k % j_days[j-1] > j_days[j-1])
//                {
//                    k = (k % j_days[j-1])+7;
//                    continue;
//                }

                dayOfMonth = k % j_days[j - 1];
                dayOfMonth = dayOfMonth == 0 ? 1 : dayOfMonth;

                var gregorianDate = Date.jalaliToGregorian(i, j, dayOfMonth);


                console.log(new Date(gregorianDate[0], gregorianDate[1], gregorianDate[2] + 1), datesArray[datesArray.length - 1]);

                if (new Date(gregorianDate[0], gregorianDate[1], gregorianDate[2] + 1) < datesArray[datesArray.length - 1]) {
                    k -= 7;
                    console.log("error");
//
//                    if(j == endMonth)
//                        break;
//                    else
                    continue;
                }

                console.log(i, j, dayOfMonth);

                datesArray.push(new Date(gregorianDate[0], gregorianDate[1], gregorianDate[2] + 1));

            }
        }
        startMonth = 1;
    }

    return datesArray;
};

Date.findDateSaturdayInJalali = function (date) { // date is Gregorian
    var jalaliDate = Date.getJalaliDate3(date),
        saturday = 1;

    while (jalaliDate.dayOfWeek != saturday) {
        date.setDate(date.getDate() - 1);
        jalaliDate = Date.getJalaliDate3(date);
    }
    return date;
};

Date.today = function () {
    var now = new Date();
    var today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    return today;
};

/* *** *** *** */

Date.prototype.addDays = function (num) {
    var value = this.valueOf();
    value += 86400000 * num;
    return new Date(value);
};

Date.prototype.addSeconds = function (num) {
    var value = this.valueOf();
    value += 1000 * num;
    return new Date(value);
};

Date.prototype.addMilliSeconds = function (num) {
    var value = this.valueOf();
    value += num;
    return new Date(value);
};

Date.prototype.addMinutes = function (num) {
    var value = this.valueOf();
    value += 60000 * num;
    return new Date(value);
};

Date.prototype.addHours = function (num) {
    var value = this.valueOf();
    value += 3600000 * num;
    return new Date(value);
};

Date.prototype.addMonths = function (num) {
    var value = new Date(this.valueOf());

    var mo = this.getMonth();
    var yr = this.getYear();

    mo = (mo + num) % 12;
    if (0 > mo) {
        yr += (this.getMonth() + num - mo - 12) / 12;
        mo += 12;
    }
    else
        yr += ((this.getMonth() + num - mo) / 12);

    value.setMonth(mo);
    value.setYear(yr);
    return value;
};

Date.prototype.monthDays = function () {
    var d = new Date(this.getFullYear(), this.getMonth() + 1, 0);
    return d.getDate();
};

Date.prototype.getPureTime = function (seconds) {
    return '{0}:{1}'.format(this.getHours(), this.getMinutes()) + (seconds ? ':' + this.getSeconds() : '');
};

Date.prototype.updateTime = function (time) {
    if (!time)
        return this;

    if (time instanceof Date)
        time = time.getPureTime(true);

    var hms = time.split(':');

    this.setHours(hms[0]);
    this.setMinutes(hms[1]);
    if (hms[2])
        this.setSeconds(hms[2]);

    return this;
};

Date.prototype.updateDate = function (date) {
    this.setFullYear(date.getFullYear());
    this.setMonth(date.getMonth());
    this.setDate(date.getDate());
    return this;
};


/*  */

function propertyTimeSetToObject(element, objectValue, property, value) {
    var timeValues = value.split(':');
    var hour = parseInt(timeValues[0]);
    var min = parseInt(timeValues[1]);
    if (timeValues.length != 2) {
        $.growl.warning({message: 'لطفا زمان را درست وارد نمائید.'});
        return false;
    }
    else if (!(hour >= 0 && hour <= 23)) {
        $.growl.warning({message: 'لطفا ساعت را درست وارد نمائید.'});
        return false;
    }
    else if (!(min >= 0 && min <= 59)) {
        $.growl.warning({message: 'لطفا دقیقه را درست وارد نمائید.'});
        return false;
    }

    var newDate = new Date(objectValue[property]);

    newDate.setHours(hour);
    newDate.setMinutes(min);

    return newDate;

//    objectValue[property].setHours(hour);
//    objectValue[property].setMinutes(min);
//    return objectValue[property];
}

