package com.pilipili.admin.Interceptor;

import static com.pilipili.Constant.CommonConstant.TOKEN_ADMIN;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.pilipili.common.ErrorCode;
import com.pilipili.exception.BusinessException;
import com.pilipili.utils.RedisUtils;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/11/23 21:47
 */
@Component
public class AppInterceptor implements HandlerInterceptor {
    // 登录页面
    private final String URL_ACCOUNT = "/account";
    private final String SWAGGER_TEST = "/doc.html";

    //文件访问
    private final String URL_FILE = "/file";

    @Resource
    private RedisUtils redisUtils;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        // 记录请求的URI
        System.out.println("请求"+request.getRequestURI());
        if(request.getRequestURI().contains(URL_ACCOUNT) || request.getRequestURI().contains(SWAGGER_TEST)){
            return true;
        }
        String token = request.getHeader(TOKEN_ADMIN);
        //获取图片的请求通过cookie中携带的token进行验证
        if (request.getRequestURI().contains(URL_FILE)){
            token = getTokenFromCookies(request);
        }
        //拿到token
        if(StringUtils.isBlank(token)){
            throw new BusinessException(ErrorCode.INVALID_TOKEN_ERROR);
        }
        //通过token进行账号验证
        String adminTokenInfo = redisUtils.getAdminTokenInfo(token);
        if (StringUtils.isBlank(adminTokenInfo)){
            throw new BusinessException(ErrorCode.INVALID_TOKEN_ERROR);
        }
        return true;
    }

    private String getTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(TOKEN_ADMIN)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
