/*
 * author: wanghuan
 * github: https://github.com/njustwh2014
 */

package cn.edu.wh.mySpringBootIoc.context;

public interface ApplicationContextInitializer<C extends ApplicationContext> {
    void initialize(C applicationContext);
}
