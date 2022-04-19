package com.xy.common.domain.annotation;

import com.xy.common.service.IChildTableMultiService;

import java.lang.annotation.*;

/**
 * 一对多子表属性标识
 * @author xiaoye
 * @create 2021-09-28 10:54
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface MultiChildTableField {

    Class<? extends IChildTableMultiService<?,?>> serviceClass();
}
