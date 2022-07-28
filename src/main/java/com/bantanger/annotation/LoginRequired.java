package com.bantanger.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Date 2022/7/28 16:14
 */

/* 自定义注解：标记在方法体上，判断是否为登陆状态 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequired {

}
