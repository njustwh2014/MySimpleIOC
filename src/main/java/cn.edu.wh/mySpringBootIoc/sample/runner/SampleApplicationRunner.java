/*
 * author: wanghuan
 * github: https://github.com/njustwh2014
 */

package cn.edu.wh.mySpringBootIoc.sample.runner;

import cn.edu.wh.mySpringBootIoc.boot.MyApplicationRunner;
import cn.edu.wh.mySpringBootIoc.context.annotation.MyAutowired;
import cn.edu.wh.mySpringBootIoc.context.annotation.MyBean;
import cn.edu.wh.mySpringBootIoc.context.annotation.MyComponent;
import cn.edu.wh.mySpringBootIoc.sample.model.BeanA;
import cn.edu.wh.mySpringBootIoc.sample.model.BeanB;
import cn.edu.wh.mySpringBootIoc.sample.model.BeanC;

@MyComponent
public class SampleApplicationRunner implements MyApplicationRunner {

    @MyAutowired
    BeanA beanA;
    @MyAutowired
    BeanB beanB;

    @MyAutowired("BeanC1")
    BeanC beanC1;
    @MyAutowired("BeanC2")
    BeanC beanC2;
    @Override
    public void run(String[] args) throws Exception {
        beanA.print();
        beanB.print();
        System.out.println(beanC1.toString());
        System.out.println(beanC2.toString());
    }

    @MyBean("BeanC1")
    private BeanC createBeanC1(){
        return new BeanC("I am bean c1!xx");
    }

    @MyBean("BeanC2")
    private BeanC createBeanC2(){
        return new BeanC("I am bean c2!xx");
    }
}
