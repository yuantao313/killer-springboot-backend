package xyz.fumarase.killer.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author YuanTao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseResult<T> implements Serializable {
    private Integer code;
    private String message;
    private Boolean status;
    private T data;

    public static  <T> ResponseResult<T> success(T data) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setStatus(true);
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    public static ResponseResult failure(ResultCode resultCode) {
        return new ResponseResult(resultCode.getCode(), resultCode.getMessage(), false, null);
    }

}
