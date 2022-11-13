package com.itheima.reggie.config;

import com.itheima.reggie.Interceptor.LoginInterceptor;
import com.itheima.reggie.common.JacksonObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    /**
     * 拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/employee/login","/employee/logout","/backend/**","/front/**","/user/sendMsg",
                        "/user/login","/category/list","/order/submit","/common/download","/order/again");
    }

    /**
     * 扩展消息转换器
     * @param converters
     */

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter messageConverter=new MappingJackson2HttpMessageConverter();
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        converters.add(0,messageConverter);
    }
}
