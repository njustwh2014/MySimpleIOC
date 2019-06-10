/*
 * author: wanghuan
 * github: https://github.com/njustwh2014
 */

package cn.edu.wh.mySpringBootIoc.context.annotation;

/*
* 关于@target和@Retention注解参考:https://www.cnblogs.com/gmq-sh/p/4798194.html
* */

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyAutowired {
    boolean required() default true;
    String value() default "";//this field is moved from @Qualifier to here for simplicity
}
