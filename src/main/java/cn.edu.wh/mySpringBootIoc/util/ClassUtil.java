/*
 * author: wanghuan
 * github: https://github.com/njustwh2014
 */

package cn.edu.wh.mySpringBootIoc.util;


import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

public class ClassUtil {
    private static final Logger LOGGER=LogUtil.getLogger(ClassUtil.class);
    /**
     * 获取指定包名下所有的类
     * @Param packagename
     * @return
     * */
    public static Set<Class<?>> getClassesByPackageName(ClassLoader classLoader,String packagename,boolean recursively) throws IOException{
        System.out.println(packagename);
        Set<Class<?>> classes=new HashSet<>();
        try{
            Enumeration<URL> urls=classLoader.getResources(packagename.replace(".","/"));
            while(urls.hasMoreElements()){
                URL url=urls.nextElement();
                if(url!=null){
                    String protocol=url.getProtocol();
                    System.out.println(protocol);
                    if("file".equals(protocol)){
                        String packagePath=url.getPath().replaceAll(" ","");
                        getClassesInPackageUsingFileProtocol(classes,classLoader,packagePath,packagename,recursively);
                    }else if("jar".equals(protocol)){
                        getClassesInPackageUsingJARProtocol(classes,classLoader,url,packagename,recursively);
                    }else{
                        LOGGER.warning(String.format("protocol[%s] is not supported!",protocol));
                    }
                }
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        return classes;
    }

    public static void getClassesInPackageUsingJARProtocol(Set<Class<?>> classes, ClassLoader classLoader, URL url,String packagename,boolean recursively) throws IOException {
        String packagePath=packagename.replaceAll(".","/");
        System.out.println("--------------getClassesInPackageUsingJARProtocol------------------------");
        JarURLConnection jarURLConnection=(JarURLConnection)url.openConnection();
        if(jarURLConnection!=null){
            JarFile jarFile=jarURLConnection.getJarFile();
            Enumeration<JarEntry> jarEntries=jarFile.entries();
            while(jarEntries.hasMoreElements()){
                JarEntry jarEntry=jarEntries.nextElement();
                String jarEntryName=jarEntry.getName();
                if(jarEntryName.startsWith(packagePath)&&jarEntryName.endsWith(".class")){
                    if(!recursively&&jarEntryName.substring(packagePath.length()+1).contains("/")){
                        continue;
                    }
                    System.out.println(jarEntryName);
                    String className=jarEntryName.substring(0,jarEntryName.lastIndexOf(".")).replaceAll("/",".");
                    classes.add(loadClass(className,false,classLoader));
                }
            }
        }
        System.out.println("--------------getClassesInPackageUsingJARProtocol------------------------");
    }

    private static void getClassesInPackageUsingFileProtocol(Set<Class<?>> classes,ClassLoader classLoader,String packagePath,String packageName,boolean recursively){
        final File[] files=new File(packagePath).listFiles(file->(file.isFile()&&file.getName().endsWith(".class")||file.isDirectory()));
        for(File file:files){
            String fileName=file.getName();
            if(file.isFile()){
                String className=fileName.substring(0,fileName.lastIndexOf("."));
                if(!StringUtil.isEmpty(packageName)){
                    className=packageName+"."+className;
                }
                classes.add(loadClass(className,false,classLoader));
            }else if(recursively){
                String subPackagePath=fileName;
                if(!StringUtil.isEmpty(subPackagePath)){
                    subPackagePath=packagePath+"/"+subPackagePath;
                }
                String subPackageName=fileName;
                if(!StringUtil.isEmpty(packageName)){
                    subPackageName=packageName+"."+subPackageName;
                }
                getClassesInPackageUsingFileProtocol(classes,classLoader,subPackagePath,subPackageName,recursively);
            }
        }
    }


    /*
    * @Param className
    * @Param isInitialized
    * @Param classsLoader
    * @return
    * */
    public static Class<?> loadClass(String className,boolean isInitialized,ClassLoader classLoader){
        Class<?> clazz;
        try{
            clazz=Class.forName(className,isInitialized,classLoader);
        }catch (ClassNotFoundException e){
            throw new RuntimeException(e);
        }
        return clazz;
    }
}

/*
* test
* */
class TestClassUtil{

    public static void main(String[] args) throws IOException{
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Set<Class<?>> classes=ClassUtil.getClassesByPackageName(classLoader,"cn.edu.wh.mySpringBootIoc",true);
        for(Class<?> clazz:classes){
            System.out.println(clazz);
        }

    }
}
