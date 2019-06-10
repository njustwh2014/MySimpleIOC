/*
 * author: wanghuan
 * github: https://github.com/njustwh2014
 */

package cn.edu.wh.mySpringBootIoc.sample.model;

public class BeanC {
    private String content;



    public BeanC(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "BeanC.content="+content;
    }
}
