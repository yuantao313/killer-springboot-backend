package xyz.fumarase.killer.model;

import io.swagger.models.auth.In;

/**
 * @author YuanTao
 */

public enum JobMode {
    //运行一次，无论是否成功
    ONCE,
    //重复运行直至成功一次
    UNTIL_SUCCESS,
    //重复运行，无论是否成功
    REPEAT;
    public int getCode(){
        return this.ordinal();
    }
    public static JobMode forValue(int value){
        return values()[value];
    }
}
