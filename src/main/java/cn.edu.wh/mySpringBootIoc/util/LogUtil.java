/*
 * author: wanghuan
 * github: https://github.com/njustwh2014
 */

package cn.edu.wh.mySpringBootIoc.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogUtil {
    private static Logger globallogger=Logger.getGlobal();

    public static void setLogger(Logger logger){globallogger=logger;}

    public static Logger getLogger(final Class<?> c){ return Logger.getLogger(c.getName());}

    public static void info(final String msg){globallogger.log(Level.INFO,msg);}
    public static String getKVLogString(final String mainMsg,final Object... objects){
        if(mainMsg==null){
            return null;
        }
        final StringBuilder sb=new StringBuilder("[".concat(mainMsg).concat("]"));
        if(objects.length!=0){
            sb.append("\t");
            int i=0;
            for(i=0;i<objects.length-1;i+=2){
                sb.append(objects[i]).append(":").append(objects[i+1]).append(";");
            }
            if(i==objects.length-1){
                sb.append(objects[i]);
            }
        }
        return sb.toString();
    }
}
