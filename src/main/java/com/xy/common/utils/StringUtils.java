package com.xy.common.utils;

/**
 * @author xiaoye
 * @create 2021-10-12 10:18
 */
public class StringUtils extends org.springframework.util.StringUtils {

    public static String getDefaultNull(String str)
    {
        return hasText(str)
                ? str
                : null;
    }

    public static String getDefaultEmpty(String str)
    {
        return hasText(str)
                ? str
                : "";
    }

    public static String getDefaultNull(Object obj)
    {
        if (obj == null)
            return null;
        return getDefaultNull(obj.toString());
    }

    public static String getDefaultEmpty(Object obj)
    {
        if (obj == null)
            return "";
        return getDefaultEmpty(obj.toString());
    }

    public static String toUnderlineCase(String str)
    {
        char[] chars = str.toCharArray();
        String result = "";
        for (char i : chars)
        {
            if (i >= 'A' && i <= 'Z')
            {
                result += "_" + String.valueOf(i).toLowerCase();
            }
            else
            {
                result += i;
            }
        }
        if (result.startsWith("_"))
        {
            result = result.substring(1);
        }
        return result;
    }

    public static String getFirstLetters(String str)
    {
        String[] s = str.split("_");
        StringBuilder builder = new StringBuilder();
        for (String s1 : s) {
            builder.append(s1.charAt(0));
        }
        return builder.toString();
    }

    public static String toCamelCase(String str)
    {
        String[] strs = str.split("_");

        StringBuilder builder = new StringBuilder();
        for (String s : strs) {
            builder.append(firstLetterToUpperCase(s));
        }
        return builder.toString();
    }

    public static String firstLetterToUpperCase(String str)
    {
        if (str.charAt(0) >= 'a' && str.charAt(0) <= 'z')
        {
            str = str.replaceFirst(String.valueOf(str.charAt(0)),String.valueOf(str.charAt(0)).toUpperCase());
        }
        return str;
    }

    public static String firstLetterToLowerCase(String str) {
        if (str.charAt(0) >= 'A' && str.charAt(0) <= 'Z')
        {
            str = str.replaceFirst(String.valueOf(str.charAt(0)),String.valueOf(str.charAt(0)).toLowerCase());
        }
        return str;
    }

    public static String setterName(String name) {
        return "set" + firstLetterToUpperCase(name);
    }

    public static String getterName(String name) {
        return "get" + firstLetterToUpperCase(name);
    }
}
