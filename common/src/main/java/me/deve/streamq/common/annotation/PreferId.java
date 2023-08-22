//-*- coding =utf-8 -*-
//@Time : 2023/8/21
//@Author: 邓闽川
//@File  PreferId.java
//@software:IntelliJ IDEA
package me.deve.streamq.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PreferId {

    String workId() default "";
    int maxIncrementCnt() default -1;

}
