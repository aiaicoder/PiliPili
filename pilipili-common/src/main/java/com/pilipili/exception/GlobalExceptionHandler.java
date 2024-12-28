package com.pilipili.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotRoleException;

import com.pilipili.common.BaseResponse;
import com.pilipili.common.ErrorCode;
import com.pilipili.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author <a href="https://github.com/liyupi">小新</a>
 * 
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        if (e instanceof NotLoginException) {
            NotLoginException notLoginException = (NotLoginException) e;
            ErrorCode errorCode = determineErrorCode(notLoginException.getType());
            String errorMessage = errorCode.getMessage();
            return ResultUtils.error(errorCode, errorMessage);
        }else if (e instanceof NotRoleException) {
            return ResultUtils.error(ErrorCode.NO_AUTH_ERROR, "无权限");
        }
        else {
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误,请联系管理员");
        }
    }

    private ErrorCode determineErrorCode(String type) {
        switch (type) {
            case NotLoginException.NOT_TOKEN:
                return ErrorCode.NOT_LOGIN_ERROR;
            case NotLoginException.INVALID_TOKEN:
                return ErrorCode.INVALID_TOKEN_ERROR;
            case NotLoginException.TOKEN_TIMEOUT:
                return ErrorCode.TOKEN_TIMEOUT_MESSAGE;
            case NotLoginException.BE_REPLACED:
                return ErrorCode.BE_REPLACED_MESSAGE;
            case NotLoginException.KICK_OUT:
                return ErrorCode.KICK_OUT_ERROR;
            case NotLoginException.TOKEN_FREEZE:
                return ErrorCode.TOKEN_FREEZE_ERROR;
        }
        return ErrorCode.SYSTEM_ERROR;
    }
}
