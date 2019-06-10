/*
 * author: wanghuan
 * github: https://github.com/njustwh2014
 */

package cn.edu.wh.mySpringBootIoc.sample;

import cn.edu.wh.mySpringBootIoc.boot.MyApplication;
import cn.edu.wh.mySpringBootIoc.boot.MyBootApplication;

@MyBootApplication
public class SampleApplication {
    
    public static void main(String[] args){
        MyApplication.run(SampleApplication.class,args);
    }
}
