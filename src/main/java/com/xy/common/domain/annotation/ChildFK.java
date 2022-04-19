package com.xy.common.domain.annotation;

import java.lang.annotation.*;

/**
 * 外键标识
 * @author xiaoye
 * @create 2021-09-28 10:51
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ChildFK {

    Class mainTableClass() default Object.class;
}
