package xyz.fumarase.killer.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author YuanTao
 */
@Component
@Data
@TableName(value = "config", autoResultMap = true)
public class ConfigModel extends Model {
    private String nickname;
    @TableId
    private Integer id;
    @JsonProperty(access=JsonProperty.Access.WRITE_ONLY)
    private String password;
    private Integer timeout;
    @TableField("school_id")
    private Integer schoolId;
}
