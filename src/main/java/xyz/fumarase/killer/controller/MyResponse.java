package xyz.fumarase.killer.controller;

import lombok.Data;

/**
 * @author YuanTao
 */
@Data
public class MyResponse {
        private Boolean status;
        private Object data;
        public MyResponse(Boolean status, Object data) {
                this.status = status;
                this.data = data;
        }
        public MyResponse(Boolean status) {
                this.status = status;
        }
}
