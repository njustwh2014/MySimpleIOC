/*
 * author: wanghuan
 * github: https://github.com/njustwh2014
 */

package cn.edu.wh.mySpringBootIoc.sample.model;

import cn.edu.wh.mySpringBootIoc.context.annotation.MyAutowired;
import cn.edu.wh.mySpringBootIoc.context.annotation.MyComponent;

@MyComponent
public class BeanA {
    private String content;
    @MyAutowired
    BeanB beanB;

    public BeanA() {
    }

    public BeanA(String content) {
        this.content = content;
    }

    public void print(){
        System.out.printf("BeanA.beanB=%s\n",beanB.toString());
    }
}
