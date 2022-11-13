package com.itheima.reggie.Interceptor;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.Utils.BaseContextUtils;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        log.info("拦截到的请求{}", requestURI);
        //检查是否登录
        Long employee = (Long) request.getSession().getAttribute("employee");
        Long user = (Long) request.getSession().getAttribute("user");
        if (employee != null) {
            long id = Thread.currentThread().getId();
            log.info("当前线程id{}", id);
            BaseContextUtils.setCurrentId(employee);
            return true;
        }
        if(user!=null){
            BaseContextUtils.setCurrentId(user);
            return true;
        }
        //未登录
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return false;
    }

}
