//package com.itheima.reggie.Filter;
//
//import com.alibaba.fastjson.JSON;
//import com.itheima.reggie.common.R;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.util.AntPathMatcher;
//
//import javax.servlet.*;
//import javax.servlet.annotation.WebFilter;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@Slf4j
//@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
//public class LoginCheckFilter implements Filter {
//    //定义一个路径匹配器
//    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        HttpServletRequest request=(HttpServletRequest) servletRequest;
//        HttpServletResponse response=(HttpServletResponse) servletResponse;
//        //获取请求的URI
//        String requestURI = request.getRequestURI();
//        log.info("拦截到的请求{}",requestURI);
//        //定义不需要过滤的方法
//        String[] urls=new String[]{
//          "/employee/login",
//          "/employee/logout",
//          "/backend/**",
//          "/front/**"
//        };
//        //判断本次请求的路径是否需要处理
//        Boolean check = check(urls, requestURI);
//        //如果不需要处理，直接放行
//        if(check){
//            log.info("本次请求{}不需要处理",requestURI);
//            filterChain.doFilter(request,response);
//            return;
//        }
//        //判断登录状态，如果已登录则直接放行
//        Object employee = request.getSession().getAttribute("employee");
//        if(employee!=null){
//            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("employee"));
//            filterChain.doFilter(request,response);
//            return;
//        }
//        //用户未登录，返回json交给前端处理
//        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
//        return;
//
//    }
//    //定义check方法，检查请求路径与urls是否匹配
//    public Boolean check(String[] urls, String requestURI){
//        for(String url:urls){
//            boolean match = PATH_MATCHER.match(url, requestURI);
//            if(match){
//                return true;
//            }
//        }
//        return false;
//    }
//}
