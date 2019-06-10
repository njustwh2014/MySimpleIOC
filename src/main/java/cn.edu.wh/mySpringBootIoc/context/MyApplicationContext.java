/*
 * author: wanghuan
 * github: https://github.com/njustwh2014
 */

package cn.edu.wh.mySpringBootIoc.context;

import cn.edu.wh.mySpringBootIoc.context.annotation.MyAutowired;
import cn.edu.wh.mySpringBootIoc.context.annotation.MyBean;
import cn.edu.wh.mySpringBootIoc.context.annotation.MyComponent;
import cn.edu.wh.mySpringBootIoc.context.factory.Bean;
import cn.edu.wh.mySpringBootIoc.util.ClassUtil;
import cn.edu.wh.mySpringBootIoc.util.ConcurrentHashSet;
import cn.edu.wh.mySpringBootIoc.util.LogUtil;
import javafx.application.Application;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public class MyApplicationContext implements ApplicationContext{
    private long startupDate;
    private Set<String> scannedPackages=new ConcurrentHashSet<>();
    private Map<String, Bean> registeredBeans=new ConcurrentHashMap<>();
    private Map<String,Bean> earlyBeans=new ConcurrentHashMap<>();
    private final Logger LOGGER= LogUtil.getLogger(this.getClass());
    // 关于Java原子类AtomicLong:https://www.cnblogs.com/kexianting/p/8554001.html
    AtomicLong totalBeanCount=new AtomicLong(0L);
    AtomicLong nameConflictCount=new AtomicLong(0L);





    @Override
    public void setStartupDate(long startupDate) {
        this.startupDate=startupDate;

    }

    @Override
    public long getStartupDate() {
        return startupDate;
    }

    @Override
    public <E> Map<String, E> getBeansOfType(Class<E> type) {
        Map<String,E> res=new HashMap<>();
        registeredBeans.entrySet().stream().filter(entry->type.isAssignableFrom(entry.getValue().getClazz())).forEach(entry->res.put(entry.getKey(),type.cast(entry.getValue().getObject())));
        return res;
    }

    @Override
    public Object getBean(String name) {
        return registeredBeans.get(name);
    }

    @Override
    public <E> E getBean(String name, Class<E> requiredType) {
        //instanceof, isinstance,isAssignableFrom的区别:https://www.cnblogs.com/exmyth/p/3164492.html
        Bean bean=(Bean)registeredBeans.get(name);
        return bean==null?null:(requiredType.isAssignableFrom(bean.getClazz())?requiredType.cast(bean.getObject()):null);
    }

    @Override
    public <E> E getBean(Class<E> requiredType) {
        Map<String,E> map=getBeansOfType(requiredType);
        return map.isEmpty()?null:requiredType.cast(map.values().toArray()[0]);
    }

    @Override
    public boolean containsBean(String name) {
        return getBean(name)!=null;
    }
    /*
    * try to autowire those beans in earlyBeans
    * if succeed remove it from earlyBeans and put it into registeredBeans
    * otherwise throw a RuntimeException(in autowireFields)
    * */
    private synchronized void processEarlyBean(){
        for(Map.Entry<String,Bean> entry:earlyBeans.entrySet()){
            Bean myBean=entry.getValue();
            try{
                if(autowireFields(myBean.getObject(),myBean.getClazz(),true)){
                    registeredBeans.put(entry.getKey(),myBean);
                    earlyBeans.remove(entry.getKey());
                }
            }catch (IllegalAccessException e){
                throw new RuntimeException(e);
            }

        }
    }


    public void scan(Set<String> basePackges,boolean recursively) throws ClassNotFoundException, IOException{
        LOGGER.info("Strat scanning......");
        ClassLoader classLoader=Thread.currentThread().getContextClassLoader();

        //get all classes who haven't been registered
        Set<Class<?>> classes=new HashSet<>();
        for(String packageName:basePackges){
            classes.addAll(ClassUtil.getClassesByPackageName(classLoader,packageName,recursively));
        }

        //Autowire or create bean for each class
        classes.forEach(this::processSingleClass);

        processEarlyBean();

        LOGGER.info("scan over!");
    }

    /*
    * try to create a bean for the certain class,if success put it into registeredBean, otherwise put it into earlyBean
    * @Param clazz
    * */
    private void processSingleClass(Class<?> clazz){
        LOGGER.info(String.format("processSingleClass[%s]",clazz.getName()));
        Annotation[] annotations=clazz.getAnnotations();
        for(Annotation annotation:annotations){
            if(annotation instanceof MyComponent){
                // create instance of the class
                Object instance;
                try{
                    instance=clazz.newInstance();
                }catch (InstantiationException e){
                    throw  new RuntimeException(e);
                }catch (IllegalAccessException e){
                    throw new RuntimeException(e);
                }
                // get bean id
                long beanid=totalBeanCount.getAndIncrement();

                MyComponent component=(MyComponent)annotation;
                String beanName=component.value();

                if(beanName.isEmpty()){
                    beanName=getBeanNameByBeanIdAndClass(clazz,beanid);
                }

                try{
                    if(autowireFields(instance,clazz,false)){
                        registeredBeans.put(beanName,new Bean(instance,clazz));
                    }else{
                        earlyBeans.put(beanName,new Bean(instance,clazz));
                    }
                }catch (IllegalAccessException e){
                    throw new RuntimeException(e);
                }
                try{
                    createBeansByMethodsOfClass(instance,clazz);
                }catch (InvocationTargetException e){
                    throw new RuntimeException(e);
                }catch (IllegalAccessException e){
                    throw new RuntimeException(e);
                }
            }
        }
    }


    /*
    *try autowire all fields of a certain instance
    * @Param instance
    * @Param clazz
    * @Param lastChance
    * @return true if success, otherwise return false or throw a exception if this is the lastChance
    * @throw IllegalAccessException
    * */
    private boolean autowireFields(Object instance, Class<?> clazz,boolean lastChance) throws IllegalAccessException{
        Field[] fields=clazz.getDeclaredFields();
        for(Field field:fields){
            Annotation[] annotations=field.getAnnotations();
            for(Annotation annotation:annotations){
                if(annotation instanceof MyAutowired){
                    MyAutowired myAutowired=(MyAutowired) annotation;
                    String beanName=myAutowired.value();
                    Bean bean=getSimpleBeanByNameOrType(beanName,field.getType(),true);
                    if(bean==null){
                        if(lastChance){
                            if(!myAutowired.required()){
                                break;
                            }
                            throw  new RuntimeException(String.format("Failed in autowireFields: [%s].[%s]",clazz.getName(),field.getName()));
                        }else{
                            return false;
                        }
                    }
                    field.setAccessible(true);
                    field.set(instance,bean.getObject());
                }
            }
        }
        return true;
    }

    private void createBeansByMethodsOfClass(Object instance,Class<?> clazz) throws InvocationTargetException,IllegalAccessException{
        List<Method> methods=getMethodsWithAnnotation(clazz, MyBean.class);
        for(Method method:methods){
            method.setAccessible(true);//忽略访问权限
            Object methodBean=method.invoke(instance);
            long beanId=totalBeanCount.getAndIncrement();
            Class<?> methodBeanClass=methodBean.getClass();

            // bean name
            MyBean myBean=method.getAnnotation(MyBean.class);
            String beanName=myBean.value();
            if(beanName.isEmpty()){
                beanName=getBeanNameByBeanIdAndClass(clazz,beanId);
            }

            //register bean
            registeredBeans.put(beanName,new Bean(methodBean,methodBeanClass));
        }
    }

    private List<Method> getMethodsWithAnnotation(Class<?> clazz,Class<?> annotationClass){
        List<Method> res=new LinkedList<>();
        Method[] methods=clazz.getDeclaredMethods();
        for(Method method:methods){
            Annotation[] annotations=method.getAnnotations();
            for(Annotation annotation:annotations){
                if(annotation.annotationType()==annotationClass){
                    res.add(method);
                    break;
                }
            }
        }
        return res;
    }
    /*
    * only used in autowireFields
    * @Param beanName
    * @Param type
    * @Param allowEarlyBeans
    * @Return
    * */
    private Bean getSimpleBeanByNameOrType(String beanName,Class<?> type,boolean allowEarlyBeans){
        //by name
        Bean res=registeredBeans.get(beanName);
        if(res==null&&allowEarlyBeans){
            res=earlyBeans.get(beanName);
        }

        //by type
        if(type!=null){
            if(res==null){
                res=getSimpleBeanByType(type,registeredBeans);
            }
            if(res==null&&allowEarlyBeans){
                res=getSimpleBeanByType(type,earlyBeans);
            }
        }

        return res;
    }


    /*search bean by type in certian bean map
    * @Param type
    * @Param beanMap
    * @return
    * */
    private Bean getSimpleBeanByType(Class<?> type,Map<String,Bean> beanMap){
        List<Bean> beans=new LinkedList<>();
        beanMap.entrySet().stream().filter(entry->type.isAssignableFrom(entry.getValue().getClazz())).forEach(entry->beans.add(entry.getValue()));
        if(beans.size()>1){
            throw new RuntimeException(String.format("Autowire by type, but more than one instance of type [%s] are founded!",beans.get(0).getClazz().getName()));
        }
        return beans.isEmpty()?null:beans.get(0);
    }


    private String getBeanNameByBeanIdAndClass(Class<?> clazz,long beanId){
        String beanName=clazz.getName()+"_"+beanId;
        if(registeredBeans.containsKey(beanName)||earlyBeans.containsKey(beanName)){
            beanName=beanName+"_"+nameConflictCount.getAndIncrement();
        }
        return beanName;
    }



}
