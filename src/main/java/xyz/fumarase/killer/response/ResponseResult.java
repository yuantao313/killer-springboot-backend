package xyz.fumarase.killer.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author YuanTao
 */
@Data
@AllArgsConstructor
public class ResponseResult implements Serializable {
        private Integer code;
        private String message;
        private Boolean status;
        private Object data;
        public ResponseResult(Object data) {
            this.status=true;
            this.code=200;
            this.message="success";
            this.data=data;
        }
}
