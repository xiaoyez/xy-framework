package com.xy.common.factory;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.convert.Convert;
import com.xy.common.domain.annotation.search.Condition;
import com.xy.common.utils.ReflectUtils;
import com.xy.common.utils.StringUtils;
import lombok.val;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * @author xiaoye
 * @create 2021-12-20 16:48
 */
public class ConditionBeanFactory {

    public static <T> T create(Class<T> beanClass)
    {
        try {
            T bean = beanClass.newInstance();
            Arrays.stream(ReflectUtils.getFields(beanClass))
                    .filter(field -> AnnotationUtil.hasAnnotation(field, Condition.class))
                    .forEach(field -> {
                        Condition condition = field.getAnnotation(Condition.class);
                        String defaultValue = condition.defaultValue();
                        if (StringUtils.hasText(defaultValue))
                        {
                            val setter = ReflectUtils.getSetterByField(field);
                            try {
                                setter.invoke(bean, Convert.convert(field.getType(),defaultValue));
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }

                    });
            return bean;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void completeDefaultValue(Object bean)
    {
        Class<?> beanClass = bean.getClass();
        Arrays.stream(ReflectUtils.getFields(beanClass))
                .filter(field -> AnnotationUtil.hasAnnotation(field, Condition.class))
                .forEach(field -> {
                    Condition condition = field.getAnnotation(Condition.class);
                    String defaultValue = condition.defaultValue();
                    if (StringUtils.hasText(defaultValue) && ReflectUtils.getFieldValue(bean,field) == null)
                    {
                        val setter = ReflectUtils.getSetterByField(field);
                        try {
                            setter.invoke(bean, Convert.convert(field.getType(),defaultValue));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }

                });
    }
}
