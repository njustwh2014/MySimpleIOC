/*
 * author: wanghuan
 * github: https://github.com/njustwh2014
 */
package cn.edu.wh.mySpringBootIoc.context.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyBean {
    String value() default "";
}
