package xyz.fumarase.killer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import xyz.fumarase.killer.anlaiye.login.exception.CaptchaInvalidException;
import xyz.fumarase.killer.anlaiye.login.exception.CaptchaTimeoutException;
import xyz.fumarase.killer.anlaiye.login.exception.LoginUnsafeException;
import xyz.fumarase.killer.response.ResponseResult;
import xyz.fumarase.killer.response.ResultCode;
import xyz.fumarase.killer.anlaiye.login.exception.LoginException;

/**
 * @author YuanTao
 */
@RestControllerAdvice
public class ExceptionHandler {
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @org.springframework.web.bind.annotation.ExceptionHandler(value = Error.class)
    public ResponseResult handler(Error e) {
        return ResponseResult.failure(ResultCode.INTERNAL_SERVER_ERROR);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = Exception.class)
    public ResponseResult handler(LoginException e) {
        if (e instanceof CaptchaInvalidException) {
            return ResponseResult.failure(ResultCode.CAPTCHA_INVALID);
        } else if (e instanceof CaptchaTimeoutException) {
            return ResponseResult.failure(ResultCode.CAPTCHA_TIMEOUT);
        } else if (e instanceof LoginUnsafeException) {
            return ResponseResult.failure(ResultCode.LOGIN_UNSAFE);
        }
        return ResponseResult.failure(ResultCode.UNKNOWN_ERROR);
    }
}
