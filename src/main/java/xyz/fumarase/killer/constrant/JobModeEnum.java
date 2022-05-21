package xyz.fumarase.killer.constrant;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author YuanTao
 */
@AllArgsConstructor
@Getter
public enum JobModeEnum {
    //运行一次，无论是否成功
    ONCE(1),
    //重复运行直至成功一次
    UNTIL_SUCCESS(2),
    //重复运行，无论是否成功
    REPEAT(3);
    @EnumValue
    @JsonValue
    public final int value;
}
