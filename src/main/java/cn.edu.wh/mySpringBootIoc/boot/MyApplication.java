/*
 * author: wanghuan
 * github: https://github.com/njustwh2014
 */
package cn.edu.wh.mySpringBootIoc.boot;

import cn.edu.wh.mySpringBootIoc.context.ApplicationContext;
import cn.edu.wh.mySpringBootIoc.context.ApplicationContextInitializer;
import cn.edu.wh.mySpringBootIoc.context.MyApplicationContext;
import cn.edu.wh.mySpringBootIoc.context.MyApplicationContextInitializer;
import cn.edu.wh.mySpringBootIoc.util.LogUtil;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;

public class MyApplication {
    private Class<?> applicationEntryClass;
    private ApplicationContext applicationContext;
    private final Logger LOGGER= LogUtil.getLogger(this.getClass());

    public MyApplication(Class<?> applicationEntryClass) {
        this.applicationEntryClass = applicationEntryClass;
    }

    public static void run(Class<?> applicationEntryClass,String[] args){
        new MyApplication(applicationEntryClass).run(args);
    }

    public void run(String[] args){
        LOGGER.info("start running...");
        //create application context and initializer
        applicationContext=createMyApplicationContext();
        ApplicationContextInitializer applicationContextInitializer=createMyApplicationContextInitailizer(applicationEntryClass);

        // initialing application context (this is where we create beans)
        applicationContextInitializer.initialize(applicationContext);// here maybe exist a hidden cast

        // process those special bean
        processSpecialBeans(args);
        LOGGER.info("Over!");



    }

    private MyApplicationContext createMyApplicationContext(){
        return new MyApplicationContext();
    }

    private MyApplicationContextInitializer createMyApplicationContextInitailizer(Class<?> entryClass){
        MyBootApplication annotation=entryClass.getDeclaredAnnotation(MyBootApplication.class);
        String[] basePackages=annotation.basePackages();
        if(basePackages.length==0){
            basePackages=new String[]{entryClass.getPackage().getName()};
        }

        //create context initializer with basepackagges
        return new MyApplicationContextInitializer(Arrays.asList(basePackages));

    }

    private void processSpecialBeans(String[] args){
        callRegisterRunners(args);
    }

    private void callRegisterRunners(String[] args){
        Map<String,MyApplicationRunner> applicationRunners=applicationContext.getBeansOfType(MyApplicationRunner.class);
        try{
            for(MyApplicationRunner myApplicationRunner:applicationRunners.values()){
                myApplicationRunner.run(args);
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
