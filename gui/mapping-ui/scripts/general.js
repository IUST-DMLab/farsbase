if (!Date.prototype.toJSON)
    Date.prototype.toJSON = function (key) {
        return this.getTime();
    };

String.prototype.toArabic = function () {

    var str = this;

    str = str.replace(/ي/g, 'ی'); // ye   \u0626
    str = str.replace(/ك/g, 'ک'); // ke

    // str = str.replace(/[\u06CC\u0649]/g, 'ي'); // ye   \u0626
    // str = str.replace(/[\u06A9]/g, 'ك'); // ke

    return str;

    //return this.replace(/ی/g, 'ي').replace(/ى/g, 'ي').replace(/ک/g, 'ك');
};

String.prototype.toArabicFree = function () {
    var str = this;
    str = str.replace(/[\u064B\u064C\u064D\u064E\u064F\u0650\u0651\u0652\u0653]/g, ''); // erab ,mad
    str = str.replace(/[\u0629]/g, 'ت'); // te gerd
    str = str.replace(/[\u0624]/g, 'و'); // vav hamze
    str = str.replace(/[\u0622\u0623\u0625]/g, 'ا'); // alef
    return str.toArabic();
};

String.prototype.numbersToPersian = function () {
    var str = this;
    for (var i = 0; i < 10; i++) {
        str = str.replace("0", "۰");
        str = str.replace("1", "۱");
        str = str.replace("2", "۲");
        str = str.replace("3", "۳");
        str = str.replace("4", "۴");
        str = str.replace("5", "۵");
        str = str.replace("6", "۶");
        str = str.replace("7", "۷");
        str = str.replace("8", "۸");
        str = str.replace("9", "۹");
    }
    return str;
};

String.prototype.numbersToEnglish = function () {
    var str = this;
    for (var i = 0; i < 10; i++) {
        str = str.replace("۰", "0");
        str = str.replace("۱", "1");
        str = str.replace("۲", "2");
        str = str.replace("۳", "3");
        str = str.replace("۴", "4");
        str = str.replace("۵", "5");
        str = str.replace("۶", "6");
        str = str.replace("۷", "7");
        str = str.replace("۸", "8");
        str = str.replace("۹", "9");
    }
    return str
};

Number.prototype.lpad = function(length, pad) {
    pad = pad || '0';
    let str = this.toString();
    while (str.length < length)
        str = pad + str;
    return str;
};

if (!String.prototype.format)
    String.prototype.format = function () {
        var args = arguments;
        return this.replace(/{(\d+)}/g, function (match, number) {
            return typeof args[number] != 'undefined' ? args[number] : match;
        });
    };

if (!String.prototype.left)
    String.prototype.left = function (length) {
        var str = this;
        if (length <= 0)
            return "";
        else if (length > String(str).length)
            return str;
        else
            return String(str).substring(0, length);
    };

if (!String.prototype.right)
    String.prototype.right = function (length) {
        var str = this;
        if (length <= 0)
            return "";
        else if (length > String(str).length)
            return str;
        else {
            var iLen = String(str).length;
            return String(str).substring(iLen, iLen - length);
        }
    };

if (!String.prototype.endsWith)
    String.prototype.endsWith = function (str) {
        return (this.match(str + "$") == str);
    };

if (!String.prototype.startsWith)
    String.prototype.startsWith = function (str) {
        return (this.match("^" + str) == str);
    };

if (!String.prototype.trim)
    String.prototype.trim = function () {
        return this.replace(/^\s*/g, '').replace(/\s*$/g, '');
    };

if (!String.prototype.firstCharToLowerCase)
    String.prototype.firstCharToLowerCase = function () {
        return this.substring(0, 1).toLowerCase() + this.substring(1);
    };

// like split but returns [] instead of ['']
String.prototype.splitex = function (splitter) {
    return (this.trim() == '') ? [] : this.split(splitter);
};

if (!String.prototype.replaceAt)
    String.prototype.replaceAt = function (index, character) {
        return this.substr(0, index) + character + this.substr(index + character.length);
    };

/* ARRAY */

if (!Array.prototype.remove)
    Array.prototype.remove = function (from, to) {
        var rest = this.slice((to || from) + 1 || this.length);
        this.length = from < 0 ? this.length + from : from;
        return this.push.apply(this, rest);
    };

if (!Array.prototype.erase)
    Array.prototype.erase = function (value) {
        var index = this.indexOf(value);
        if (index == -1)
            return undefined;
        return this.remove(index);
    };

if (!Array.prototype.exists)
    Array.prototype.exists = function (value) {
        return this.indexOf(value) > -1;
    };

if (!Array.prototype.clear)
    Array.prototype.clear = function () {
        this.length = 0;
    };

if (!Array.prototype.insert)
    Array.prototype.insert = function (index, item) {
        this.splice(index, 0, item);
    };

if (!Array.prototype.clone)
    Array.prototype.clone = function () {
        return this.slice(0);
    };

if (!Array.prototype.first)
    Array.prototype.first = function () {
        return this.length > 0 ? this[0] : undefined;
    };

if (!Array.prototype.last)
    Array.prototype.last = function () {
        return this.length > 0 ? this[this.length - 1] : undefined;
    };

if (!Array.prototype.filterex)
    Array.prototype.filterex = function (filter, args) {
        var result = [];
        for (var i = 0, l = this.length; i < l; i++) {
            var params = [this[i]].concat(args);
            if (filter.apply(null, params)) {  // here callback is called with the current element
                result.push(this[i]);
            }
        }
        return result;
    };

if (!Array.prototype.choose)
    Array.prototype.choose = function (field, value) {
        return this.filter(function (x) {
            return x[field] === value;
        });
    };

if (!Array.prototype.projection)
    Array.prototype.projection = function (field) {
        return this.map(function (x) {
            return x[field];
        });
    };

if (!Array.prototype.projector)
    Array.prototype.projector = function (projector) {
        return this.map(function (x) {
            return x[projector].call(x);
        });
    };

if (!Array.prototype.enclose)
    Array.prototype.enclose = function (start, end) {
        end = end || start;
        return this.map(function (x) {
            return start + x + end;
        });
    };


if (!Array.prototype.except)
    Array.prototype.except = function (a) {
        return this.filter(function (i) {
            return a.indexOf(i) < 0;
        });
    };

if (!Array.prototype.max)
    Array.prototype.max = function () {
        return Math.max.apply(Math, this);
    };

if (!Array.prototype.min)
    Array.prototype.min = function () {
        return Math.min.apply(Math, this);
    };

if (!Array.prototype.concatArrays)
    Array.prototype.concatArrays = function () {
        var tempArray = [];
        this.forEach(function (array) {
            tempArray = tempArray.concat(array);
        });
        return tempArray;
    };

if (!Array.prototype.replace)
    Array.prototype.replace = function (oldValue, newValue) {
        var idx = this.indexOf(oldValue);
        this[idx] = newValue;
        return this;
    };

if (!Array.prototype.flatten)
    Array.prototype.flatten = function () {
        var ret = [];
        for (var i = 0; i < this.length; i++) {
            if (Array.isArray(this[i])) {
                ret = ret.concat(this[i].flatten());
            } else {
                ret.push(this[i]);
            }
        }
        return ret;
    };

if (!Array.prototype.joinWith)
    Array.prototype.joinWith = function (enclose, delimiter) {
        return enclose + this.join(enclose + delimiter + enclose) + enclose;
    };

if (!Object.toArray)
    Object.toArray = function () {
        var object = this;
        var a = [];
        var i = 0;
        for (var p in object)
            if ((typeof object[p] !== 'function') && object.hasOwnProperty(p))
                a.push({key: p, value: object[p], index: i++});

        return a;
    };


/*
 function parseSearch(text) {

 return text
 .split(/("[^"]*")/g)
 .map(function(x){return x.trim().startsWith('"') ? x : x.split(/\s/g)})
 .flatten()
 .filter(function(x){return x.trim().length!==0;})
 .map(function(x){return x.trim();});

 }

 var g_days = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
 var j_days = [31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29];
 function gregorianToJalali (g_y, g_m, g_d) {
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
 }

 function getTodayShamsi() {
 var date = new Date();
 var y = date.getFullYear();
 var m = date.getMonth();
 var d = date.getDate();
 return gregorianToJalali(y,m,d);
 }

 function getTodayShamsiShort(reverse) {
 if (reverse){
 var sh = getTodayShamsi();
 return [sh[2],sh[1],sh[0]].join('/');
 }
 return getTodayShamsi().join('/');
 }



 */