package com.xy.common.utils;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ReflectUtil;
import com.xy.common.domain.annotation.MultiChildTableField;
import com.xy.common.domain.annotation.SingleChildTableField;
import lombok.val;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaoye
 * @create 2021-10-20 18:11
 */
public class ReflectUtils extends ReflectUtil {

    public static List<String> getFieldNames(Class clazz)
    {
        List<String> fieldNames = Arrays.stream(getFields(clazz))
                .map(field -> {
                    return field.getName();
                })
                .collect(Collectors.toList());
        return fieldNames;
    }

    public static Method getSetterByField(Field field)
    {
        val setterName = StringUtils.setterName(field.getName());

        return getMethod(field.getDeclaringClass(),setterName,field.getType());
    }

    public static Method getGetterByField(Field field)
    {
        val getterName = StringUtils.getterName(field.getName());

        return getMethod(field.getDeclaringClass(),getterName);
    }

    public static void setFieldValueWhenNull(Object obj, String fieldName, Object value)
    {
        Class<?> objClass = obj.getClass();
        if (!hasField(objClass,fieldName))
            return;
        if (getFieldValue(obj,fieldName) == null)
            setFieldValue(obj,fieldName,value);
    }

    public static void setFieldValue(Object obj, String fieldName, Object value)
    {
        Class<?> objClass = obj.getClass();
        if (!hasField(objClass,fieldName))
            return;
        ReflectUtil.setFieldValue(obj,fieldName,value);
    }

    public static List<Object> getSingleChildTableFieldsIgnoreNull(Object domain){

        Class<?> domainClass = domain.getClass();
        return Arrays.stream(domainClass.getDeclaredFields())
                .filter(field ->
                    AnnotationUtil.hasAnnotation(field, SingleChildTableField.class)
                )
                .filter(field -> getFieldValue(domain,field) != null)
                .map(field -> getFieldValue(domain,field))
                .collect(Collectors.toList());
    }

    public static List<List<?>> getMultiChildTableFieldsIgnoreNull(Object domain){

        Class<?> domainClass = domain.getClass();
        return Arrays.stream(domainClass.getDeclaredFields())
                .filter(field ->
                        AnnotationUtil.hasAnnotation(field, MultiChildTableField.class)
                )
                .filter(field -> !CollectionUtils.isEmpty((List)getFieldValue(domain,field)))
                .map(field -> (List<?>)getFieldValue(domain,field))
                .collect(Collectors.toList());
    }
}
