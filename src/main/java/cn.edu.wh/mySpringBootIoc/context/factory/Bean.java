/*
 * author: wanghuan
 * github: https://github.com/njustwh2014
 */

package cn.edu.wh.mySpringBootIoc.context.factory;

public class Bean {
    private Object object;
    private Class<?> clazz;
    public Bean(Object object,Class<?> clazz){
        this.object=object;
        this.clazz=clazz;
    }

    public Object getObject() {
        return object;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
