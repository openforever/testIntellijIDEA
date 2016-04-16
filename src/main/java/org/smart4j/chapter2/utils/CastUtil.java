package org.smart4j.chapter2.utils;

/**
 * Created by snow on 2016/4/16.
 * 转型操作工具类
 */
public class CastUtil {

    /*转为String*/
    public static String castString(Object obj) {
        return CastUtil.castString(obj, "");
    }

    /*转为String(可指定默认值)*/
    public static String castString(Object obj, String defaultValue) {
        return obj != null ? String.valueOf(obj) : defaultValue;
    }

    /*转为double*/
    public static double castDouble(Object obj){
        return castDouble(obj, 0);
    }

    /*转为double(提供默认值)*/
    public static double castDouble(Object obj, int defaultValue) {
        double doubleValue = defaultValue;
        if (obj != null){
            String strValue = castString(obj);
            if (StringUtil.isNotEmpty(strValue)){
                try {
                    doubleValue = Double.parseDouble(strValue);
                } catch (NumberFormatException e) {
                    doubleValue = defaultValue;
                }
            }
        }
        return doubleValue;
    }

    /*转为long型*/
    public static long castLong(Object obj){
        return CastUtil.castLong(obj, 0);
    }

    /*转为long型(可指定默认值)*/
    public static long castLong(Object obj, int defaultValue) {
        long longValue = defaultValue;
        if (obj != null){
            String strValue = castString(obj);
            if (StringUtil.isNotEmpty(strValue)){
                try {
                    longValue = Long.parseLong(strValue);
                } catch (NumberFormatException e) {
                    longValue = defaultValue;
                }
            }
        }
        return longValue;
    }

    /*转为int型*/
    public static int castInt(Object obj) {
        return CastUtil.castInt(obj, 0);
    }

    /*转为int型(可指定默认值)*/
    public static int castInt(Object obj, int defaultValue) {
        int intValue = defaultValue;
        if (obj != null){
            String strValue = castString(obj);
            if (StringUtil.isNotEmpty(strValue)){
                try {
                    intValue = Integer.parseInt(strValue);
                } catch (NumberFormatException e) {
                    intValue = defaultValue;
                }
            }
        }
        return intValue;
    }

    /*转为boolean型(默认值为false)*/
    public static boolean castBoolean(Object obj) {
        return CastUtil.castBoolean(obj, false);
    }

    /*转为boolean型(可指定默认值)*/
    public static boolean castBoolean(Object obj, boolean defaultValue) {
        boolean booleanValue = defaultValue;
        if (obj != null){
            booleanValue = Boolean.parseBoolean(castString(obj));
        }
        return booleanValue;
    }

}
