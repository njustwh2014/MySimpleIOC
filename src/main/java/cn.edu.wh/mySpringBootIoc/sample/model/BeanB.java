/*
 * author: wanghuan
 * github: https://github.com/njustwh2014
 */

package cn.edu.wh.mySpringBootIoc.sample.model;

import cn.edu.wh.mySpringBootIoc.context.annotation.MyAutowired;
import cn.edu.wh.mySpringBootIoc.context.annotation.MyComponent;

@MyComponent
public class BeanB {
    private String content;

    public BeanB() {
    }

    public BeanB(String content) {
        this.content = content;
    }

    @MyAutowired
    BeanA beanA;
    public void print(){
        System.out.printf("BeanB.beanA=%s\n",beanA.toString());
    }
}
