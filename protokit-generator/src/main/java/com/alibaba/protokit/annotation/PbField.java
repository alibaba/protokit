package com.alibaba.protokit.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TODO 直接叫 `@Tag` 是否就可以了？
 * 
 * TODO 增加 from/to ，用户可以显式指定转换的函数？
 * 
 * @author hengyunabc 2021-01-18
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
@Documented
public @interface PbField {

    int tag();

    Type type() default Type.UNKNOWN;

    String comment() default "";
}