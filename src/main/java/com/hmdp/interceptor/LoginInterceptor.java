package com.hmdp.interceptor;

import com.hmdp.utils.UserHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 1.RefreshTokenInterceptor之后执行<br/>
 * 2.判断ThreadLocal中是否有用户,如果没有就拦截并返回 401状态码<br/>
 * 3.如果用户有, 则放行<br/><br/>
 * 专注于验证用户登录的逻辑，如果路径需要登录，但用户未登录，则直接拦截请求
 */
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.判断是否需要拦截（ThreadLocal）中是否有用户
        if (UserHolder.getUser() == null) {
            //没有需要拦截，设置状态码
            response.setStatus(401);
            //拦截
            return false;
        }
        //由用户放行
        return true;
    }
}