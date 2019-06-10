/*
 * author: wanghuan
 * github: https://github.com/njustwh2014
 */

package cn.edu.wh.mySpringBootIoc.context;

import cn.edu.wh.mySpringBootIoc.context.factory.ListableBeanFactory;

public interface ApplicationContext extends ListableBeanFactory {
    void setStartupDate(long startupDate);
    long getStartupDate();
}
