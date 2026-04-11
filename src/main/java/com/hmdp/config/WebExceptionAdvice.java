package com.hmdp.config;

import com.hmdp.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 这是一个全局异常处理器，功能如下：<br/>
 * 1.使用 @RestControllerAdvice 拦截所有控制器的异常<br/>
 * 2.专门捕获 RuntimeException 运行时异常<br/>
 * 3.记录错误日志并返回统一的失败结果 "服务器异常"<br/>
 * 实现了对未捕获异常的集中处理和统一响应。
 */
@Slf4j
@RestControllerAdvice
public class WebExceptionAdvice {
    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(RuntimeException e) {
        log.error(e.toString(), e);
        return Result.fail("服务器异常");
    }
}