package com.jxufe.utils;

import com.jxufe.constant.ExceptionConstant;

public class StringUtil {

    public static String subString(String str, Integer number){

        if( str.isBlank() ){
            throw new RuntimeException(ExceptionConstant.STR_EXCEPTION);
        }

        int length = str.length();
        return str.substring( length - number);
    }
}
