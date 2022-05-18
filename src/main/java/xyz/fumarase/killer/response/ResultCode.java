package xyz.fumarase.killer.response;

import lombok.Getter;

/**
 * @author YuanTao
 */

@Getter
@SuppressWarnings("AlibabaEnumConstantsMustHaveComment")
public enum ResultCode {
    SUCCESS(0, "成功"),
    FAILURE(-1, "失败"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    CAPTCHA_INVALID(1001, "验证码错误"),
    CAPTCHA_TIMEOUT(1002, "验证码超时"),
    LOGIN_UNSAFE(1003, "登录不安全"), UNKNOWN_ERROR(-100,"未知错误" );
    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}

