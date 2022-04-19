package com.xy.common.domain;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ReflectUtil;
import com.xy.common.domain.annotation.ChildFK;
import com.xy.common.domain.exception.ForeignKeyNotFoundException;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaoye
 * @create 2021-09-23 16:50
 */
public interface IChildTableMultiObject{

    default void setFK(Object fk)
    {
        Class<? extends IChildTableMultiObject> childTableClass = this.getClass();
        Field[] fields = ReflectUtil.getFields(childTableClass);
        List<Field> fkList = Arrays.stream(fields)
                .filter(field -> {
                    return AnnotationUtil.getAnnotation(field, ChildFK.class) != null;
                })
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(fkList))
            throw new ForeignKeyNotFoundException("未找到被@FK标注的属性，类型为: " + childTableClass.getName());
        Field field = fkList.get(0);
        ReflectUtil.setFieldValue(this,field,fk);
    }

    default void setFK(Object fk,Class mainTableClass)
    {
        Class<? extends IChildTableMultiObject> childTableClass = this.getClass();
        Field[] fields = ReflectUtil.getFields(childTableClass);
        List<Field> fkList = Arrays.stream(fields)
                .filter(field -> {
                    return AnnotationUtil.getAnnotation(field, ChildFK.class) != null;
                })
                .filter(field -> {
                    return AnnotationUtil.getAnnotation(field, ChildFK.class).mainTableClass() == mainTableClass;
                })
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(fkList))
            throw new ForeignKeyNotFoundException("未找到被@ChildFK标注的属性，类型为: " + childTableClass.getName());
        Field field = fkList.get(0);
        ReflectUtil.setFieldValue(this,field,fk);
    }

    default int getFkCount()
    {
        Class<? extends IChildTableMultiObject> childTableClass = this.getClass();
        Field[] fields = ReflectUtil.getFields(childTableClass);
        List<Field> fkList = Arrays.stream(fields)
                .filter(field -> {
                    return AnnotationUtil.getAnnotation(field, ChildFK.class) != null;
                })
                .collect(Collectors.toList());
        return fkList.size();
    }
}
