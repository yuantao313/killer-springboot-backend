package xyz.fumarase.killer.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author YuanTao
 */
@Data
@TableName(value = "user", autoResultMap = true)
public class UserModel extends ModelBase {
    private Integer id;

    @TableId
    @TableField("user_id")
    private Long userId;

    @TableField("login_token")
    private String loginToken;

    private String token;

    @TableField("add_time")
    private Date addTime;

    @TableField(value = "update_time", update = "now()")
    private Date updateTime;
}
