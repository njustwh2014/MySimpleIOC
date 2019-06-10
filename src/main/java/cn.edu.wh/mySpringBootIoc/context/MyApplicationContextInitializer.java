/*
 * author: wanghuan
 * github: https://github.com/njustwh2014
 */

package cn.edu.wh.mySpringBootIoc.context;


import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MyApplicationContextInitializer implements ApplicationContextInitializer<MyApplicationContext> {
    private Set<String> basePackages=new LinkedHashSet<>();

    public MyApplicationContextInitializer(List<String> basePackages) {
        this.basePackages.addAll(basePackages);
    }

    @Override
    public void initialize(MyApplicationContext myApplicationContext) {
        try{
            myApplicationContext.scan(basePackages,true);
        }catch (ClassNotFoundException e){
            throw new RuntimeException(e);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        myApplicationContext.setStartupDate(System.currentTimeMillis());
    }
}
