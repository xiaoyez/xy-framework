package com.xy.common.utils;

import cn.hutool.core.util.ReflectUtil;
import com.xy.common.domain.IMainTableObject;
import lombok.SneakyThrows;
import org.springframework.core.annotation.AnnotationUtils;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * @author xiaoye
 * @create 2021-10-11 17:40
 */
public class ObjectUtils {

    /**
     * 获取domain的主键的值
     * @param domain
     * @return
     */
    @SneakyThrows
    public static Object getPrimaryKey(Object domain)
    {
        if (domain == null)
            return null;
        Class<?> domainClass = domain.getClass();
        Field[] declaredFields = domainClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            Id annotation = AnnotationUtils.findAnnotation(declaredField, Id.class);
            if (annotation != null)
            {
                return ReflectUtils.getFieldValue(domain,declaredField);
            }
        }
        return null;
    }

    public static void ignoreEmptyString(Object obj)
    {
        Class<?> objClass = obj.getClass();
        Arrays.stream(objClass.getDeclaredFields())
                .filter(field -> {
                    return field.getType() == String.class;
                })
                .forEach(field -> {
                    if ("".equals(ReflectUtil.getFieldValue(obj,field)))
                        ReflectUtil.setFieldValue(obj,field,null);
                });
    }

    /**
     * 默认获取，若obj为null，就返回defaultValue.否则返回obj
     * @param obj
     * @param defaultValue
     * @param <T>
     * @return
     */
    public static <T> T getDefault(T obj, T defaultValue)
    {
        return obj == null ? defaultValue : obj;
    }

    public static <T extends IMainTableObject> void setPrimaryKey(T domain, Object pk) {
        if (domain == null)
            return;
        Class<?> domainClass = domain.getClass();
        Field[] declaredFields = domainClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            Id annotation = AnnotationUtils.findAnnotation(declaredField, Id.class);
            if (annotation != null)
            {
                ReflectUtils.setFieldValue(domain,declaredField,pk);
            }
        }
    }
}
