package com.itheima.reggie.Utils;

import org.springframework.stereotype.Component;

@Component
public class BaseContextUtils {
    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }
    public static Long getCurrentId(){
        return threadLocal.get();
    }

}
