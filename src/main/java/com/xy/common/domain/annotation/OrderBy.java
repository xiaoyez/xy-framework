package com.xy.common.domain.annotation;

import java.lang.annotation.*;

/**
 * @author xiaoye
 * @create 2021-10-18 16:33
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface OrderBy {

    public String value() default "ASC";

    String ASC = "ASC", DESC = "DESC";
}
