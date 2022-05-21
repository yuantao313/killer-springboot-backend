package xyz.fumarase.killer.constrant;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public enum HistoryStatusEnum {
    UNKNOWN(-1, "UNKNOWN"),
    SUCCESS(1, "SUCCESS"),
    RUNNING(0, "RUNNING"),
    PARTIALLY(2, "PARTIALLY"),
    TIMEOUT(3, "TIMEOUT"),
    EMPTY_ORDER(4, "EMPTY ORDER"),
    TOKEN_INVALID(5, "TOKEN INVALID"),
    ;
    @EnumValue
    public int num;
    @JsonValue
    public String desc;
    }
