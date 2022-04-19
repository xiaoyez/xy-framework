package com.xy.common.domain.annotation.search;

import java.lang.annotation.*;

/**
 * @author xiaoye
 * @create 2021-10-13 11:05
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Condition {

    String defaultValue() default "";
}
