package com.pilipili.admin.Interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/11/23 21:45
 */
@Configuration
public class WebAppConfigurer implements WebMvcConfigurer {

    @Resource
    private AppInterceptor appInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(appInterceptor).excludePathPatterns("/admin/swagger-ui.html","/admin/swagger-resources/**","/admin/webjars/**","/admin/swagger-ui/**","admin/v2/api-docs/**","/admin/doc.html/**");
    }
}
