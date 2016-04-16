package org.smart4j.chapter2.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by snow on 2016/4/16.
 * 字符串工具类
 */
public class StringUtil {
    /*判断字符串是否为空,并去掉字符串末尾空白符*/
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /*判断字符串是否非空,并去掉字符串末尾空白符*/
    public static boolean isEmpty(String str){
        if (str != null){
            str = str.trim();
        }
        return StringUtils.isNotEmpty(str);
    }
}
