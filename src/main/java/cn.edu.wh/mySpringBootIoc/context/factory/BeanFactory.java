/*
 * author: wanghuan
 * github: https://github.com/njustwh2014
 */

package cn.edu.wh.mySpringBootIoc.context.factory;

public interface BeanFactory {
    Object getBean(String name);
    <E> E getBean(String name,Class<E> requiredType);
    <E> E getBean(Class<E> requiredType);
    boolean containsBean(String name);
}
