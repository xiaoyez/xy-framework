package com.xy.common.domain.annotation;

import java.lang.annotation.*;

/**
 * 被子表引用的键
 * @author xiaoye
 * @create 2021-09-29 14:42
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface MainRefKey {
}
