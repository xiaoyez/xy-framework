package com.xy.common.domain.annotation;

import java.lang.annotation.*;

/**
 * @author xiaoye
 * @create 2021-11-04 11:37
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Force {
}
