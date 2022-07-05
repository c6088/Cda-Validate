package com.bayc.xsdvalidatecmd.control;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 我的公共操作类
 */
public class GCLib {
    /**
     * 生成重复字符串
     *
     * @param str 字符串
     * @param n   重复次数
     * @return 字符串
     */
    public static String RepeatStr(String str, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    /**
     * 是不是实数字符串，0-9而且最好有一个小数点
     *
     * @param str 字符串
     * @return true是 false否
     */
    public static boolean IsDoubleStr(String str) {
        Boolean isOk = true;
        int charLen = str.length();
        int dotCount = 0;
        for (int i = 0; i < charLen; i++) {
            char c = str.charAt(i);
            if (!Character.isDigit(c)) {
                if (c == '.') {
                    dotCount++;
                    if (dotCount >= 2) {
                        isOk = false;
                        break;
                    }
                } else {
                    isOk = false;
                    break;
                }
            }
        }
        return isOk;
    }

    /**
     * 是不是数字字符串，0-9组成
     *
     * @param str 字符串
     * @return true是 false否
     */
    public static boolean IsNumberStr(String str) {
        Boolean isOk = true;
        int charLen = str.length();
        for (int i = 0; i < charLen; i++) {
            char c = str.charAt(i);
            if (!Character.isDigit(c)) {
                isOk = false;
                break;
            }
        }
        return isOk;
    }

    /**
     * 是否是null或者空字符串
     *
     * @param str 字符串
     * @return true是 false否
     */
    public static boolean IsNullAndEmpty(String str) {
        return (str == null || str.isEmpty());
    }

    /**
     * 是否是null或者空字符串
     *
     * @param str 字符串
     * @return true是 false否
     */
    public static boolean IsNullAndWhiteSpace(String str) {
        if (str == null || str.isEmpty()) {
            return true;
        }
        String v = str.trim();
        return v.isEmpty();
    }

    /**
     * 对象转字符串， null为“”
     *
     * @param o 对象
     * @return 字符串
     */
    public static String CStr(Object o) {
        if (o == null) return "";
        return o.toString();
    }

    /**
     * 字符串Trim， null为“”
     *
     * @param str 字符串
     * @return 字符串
     */
    public static String CTrim(String str) {
        if (str == null) return "";
        return str.trim();
    }

    /**
     * 对象转字符串Trim, null为“”
     *
     * @param o 对象
     * @return 字符串
     */
    public static String CTrim(Object o) {
        if (o == null) return "";
        return o.toString().trim();
    }

    /**
     * 字符串转换为整数，不成功则为0
     *
     * @param str 字符串
     * @return 整数
     */
    public static int CInt(String str) {
        if (str == null || str.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(str.trim());
        } catch (Exception ex) {
        }
        return 0;
    }

    /**
     * 字符串转换为双精度浮点数，不成功则为0
     *
     * @param str 字符串
     * @return 双精度浮点数
     */
    public static Double CDouble(String str) {
        if (str == null || str.isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(str.trim());
        } catch (Exception ex) {
        }
        return 0.0;
    }

    /**
     * 字符串转换为BigDecimal，不成功则为0。0
     *
     * @param str 字符串
     * @return 货币型Decimal
     */
    public static BigDecimal CDecimal(String str) {
        Double dbl = CDouble(str);
        try {
            return BigDecimal.valueOf(dbl);
        } catch (Exception ex) {
        }
        return BigDecimal.valueOf(0);
    }

    /**
     * 格式字符串yyyy-MM-dd转换为Date
     *
     * @param str 字符串
     * @return 日期Date
     */
    public static Date CDate(String str) {
        return CDate(str, "yyyy-MM-dd");
    }

    /**
     * 字符串转换为Date，标明转换格式
     *
     * @param str       字符串
     * @param formatStr 格式字符串
     * @return 日期Date
     */
    public static Date CDate(String str, String formatStr) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        try {
            date = sdf.parse(str);
        } catch (Exception ex) {
        }
        return date;
    }

    /**
     * 格式字符串yyyy-MM-dd hh:mm:ss转换为Date
     *
     * @param str 字符串
     * @return 日期Date
     */
    public static Date CDateTime(String str) {
        return CDate(str, "yyyy-MM-dd hh:mm:ss");
    }


    /**
     * Date转换为yyyy-MM-dd格式的字符串
     *
     * @param date 日期
     * @return 字符串
     */
    public static String ToDateStr(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
        return dateFormat.format(date);
    }

    /**
     * Date转换为yyyy-MM-dd hh:mm:ss格式的字符串
     *
     * @param date 日期
     * @return 字符串
     */
    public static String ToDateTimeStr(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd yyyy-MM-dd hh:mm:ss");
        return dateFormat.format(date);
    }
}
