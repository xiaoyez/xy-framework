package com.xy.common.domain;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ReflectUtil;
import com.xy.common.domain.annotation.MainRefKey;
import com.xy.common.domain.annotation.MultiChildTableField;
import com.xy.common.domain.annotation.SingleChildTableField;
import com.xy.common.domain.exception.ForeignKeyNotFoundException;
import com.xy.common.service.IBaseService;
import com.xy.common.service.IChildTableMultiService;
import com.xy.common.utils.spring.SpringUtils;
import org.springframework.util.CollectionUtils;
import com.xy.common.mapper.Mapper;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 主表对象
 * @author xiaoye
 * @create 2021-09-23 16:48
 */
public interface IMainTableObject {

    /**
     * 获取子表对主表的外键
     * @return
     */
    default Object getFK()
    {
        Class<? extends IMainTableObject> mainTableClass = this.getClass();
        Field[] fields = ReflectUtil.getFields(mainTableClass);
        List<Field> fkList = Arrays.stream(fields)
                .filter(field -> {
                    return AnnotationUtil.getAnnotation(field, MainRefKey.class) != null;
                })
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(fkList))
            throw new ForeignKeyNotFoundException("未找到被@FK标注的属性，类型为: " + mainTableClass.getName());
        Field field = fkList.get(0);
        return ReflectUtil.getFieldValue(this,field);
    }

    /**
     * 获取子表完全对象
     * @return
     */
    default ChildTableCompleteObject childTableCompleteObject()
    {
        ChildTableCompleteObject childTableCompleteObject = new ChildTableCompleteObject();
        Class<? extends IMainTableObject> mainTableClass = this.getClass();
        Field[] fields = ReflectUtil.getFields(mainTableClass);
        List<ChildTableCompleteObject.ChildTableSingleCompleteObject<IChildTableSingleObject, IBaseService<IChildTableSingleObject, ?>>> simpleChildTableList = Arrays.stream(fields)
                .filter((field) -> {
                    return AnnotationUtil.getAnnotation(field, SingleChildTableField.class) != null;
                })
                .map((field) -> {
                    SingleChildTableField annotation = AnnotationUtil.getAnnotation(field, SingleChildTableField.class);
                    ChildTableCompleteObject.ChildTableSingleCompleteObject<IChildTableSingleObject, IBaseService<IChildTableSingleObject, ? extends Mapper<IChildTableSingleObject>>> childTableSingleCompleteObject = new ChildTableCompleteObject.ChildTableSingleCompleteObject<>();
                    childTableSingleCompleteObject.setObject((IChildTableSingleObject) ReflectUtil.getFieldValue(this, field));
                    IBaseService<?, ?> service = SpringUtils.getBean(annotation.serviceClass());
                    childTableSingleCompleteObject.setService((IBaseService<IChildTableSingleObject, ? extends Mapper<IChildTableSingleObject>>) service);
                    return childTableSingleCompleteObject;
                })
                .collect(Collectors.toList());
        List<ChildTableCompleteObject.ChildTableMultiCompleteObject<IChildTableMultiObject, IChildTableMultiService<IChildTableMultiObject, ?>>> multiChildTableList = Arrays.stream(fields)
                .filter((field) -> {
                    return AnnotationUtil.getAnnotation(field, MultiChildTableField.class) != null;
                })
                .map((field) -> {
                    MultiChildTableField annotation = AnnotationUtil.getAnnotation(field, MultiChildTableField.class);
                    ChildTableCompleteObject.ChildTableMultiCompleteObject<IChildTableMultiObject, IChildTableMultiService<IChildTableMultiObject, ?>> childTableMultiCompleteObject = new ChildTableCompleteObject.ChildTableMultiCompleteObject<>();
                    childTableMultiCompleteObject.setObjects((List<IChildTableMultiObject>) ReflectUtil.getFieldValue(this, field));
                    IBaseService<?, ?> service = SpringUtils.getBean(annotation.serviceClass());
                    childTableMultiCompleteObject.setService((IChildTableMultiService<IChildTableMultiObject, ?>) service);
                    return childTableMultiCompleteObject;
                })
                .collect(Collectors.toList());
        childTableCompleteObject.setSingleCompleteObjects(simpleChildTableList);
        childTableCompleteObject.setMultiCompleteObjects(multiChildTableList);
        return childTableCompleteObject;
    }
}
