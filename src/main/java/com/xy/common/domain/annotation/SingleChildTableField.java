package com.xy.common.domain.annotation;

import com.xy.common.service.IBaseService;

import java.lang.annotation.*;

/**
 * 一对一子表属性标识
 * @author xiaoye
 * @create 2021-09-28 10:52
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface SingleChildTableField {

    Class<? extends IBaseService<?,?>> serviceClass();
}
