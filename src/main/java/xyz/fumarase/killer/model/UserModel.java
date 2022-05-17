package xyz.fumarase.killer.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author YuanTao
 */
@Component
@Data
@TableName(value = "user", autoResultMap = true)
public class UserModel extends Model{
    @TableId
    @TableField("user_id")
    private Long userId;
    @TableField("login_token")
    private String loginToken;
    private String token;


    @TableField("add_time")
    private String addTime;
    @TableField("update_time")
    private String updateTime;
}
