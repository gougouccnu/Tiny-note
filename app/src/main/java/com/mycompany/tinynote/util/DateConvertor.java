package com.mycompany.tinynote.util;

/**
 * Created by lishaowei on 16/2/19.
 */
public class DateConvertor {
    /**
     * 将int year转换为中文年，比如2016转换为二零一六年
     * @param year
     * @return
     */
    public static String formatYear(int year) {
        StringBuffer str = new StringBuffer();
        int num4, num3, num2, num1;
        num4 = year / 1000;
        num3 = (year - num4 * 1000) / 100;
        num2 = (year - num4 * 1000 - num3 * 100) / 10;
        num1 = year - num4 * 1000 - num3 * 100 - num2 * 10;
        str.append(formatDigit(num4));
        str.append(formatDigit(num3));
        str.append(formatDigit(num2));
        str.append(formatDigit(num1));
        str.append('年');
        return str.toString();
    }

    /**
     * 将int day转换为中文日，比如3转化为四日
     * @param day
     * @return
     */
    public static String formatDay(int day) {
        StringBuffer str = new StringBuffer();
        int currentDay = day + 1;
        if (currentDay == 10 || currentDay == 20 || currentDay == 30) {
            str.append(formatDigit(currentDay / 10));
            str.append('拾');
            str.append('日');
        }
        if (currentDay < 10) {
            str.append(formatDigit(currentDay));
            str.append('日');
        }
        else {
            str.append(formatDigit((currentDay / 10)));
            str.append('拾');
            str.append(formatDigit((currentDay % 10)));
            str.append('日');
        }
        return str.toString();
    }

    /**
     * 将数字转换为中文 如 1--》一
     * @param digital
     * @return
     */
    private static char formatDigit(int digital) {
        char value;
        switch (digital) {
            case 0:  value = '零'; break;
            case 1:  value = '一'; break;
            case 2:  value = '二'; break;
            case 3:  value = '三'; break;
            case 4:  value = '四'; break;
            case 5:  value = '五'; break;
            case 6:  value = '六'; break;
            case 7:  value = '七'; break;
            case 8:  value = '八'; break;
            case 9:  value = '九'; break;
            default: value = 'E';
        }
        return value;
    }
}
