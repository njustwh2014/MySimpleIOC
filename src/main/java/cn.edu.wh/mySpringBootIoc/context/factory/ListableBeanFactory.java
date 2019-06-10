/*
 * author: wanghuan
 * github: https://github.com/njustwh2014
 */

package cn.edu.wh.mySpringBootIoc.context.factory;

import java.util.Map;

public interface ListableBeanFactory extends BeanFactory {
    <E> Map<String,E> getBeansOfType(Class<E> type);
}
