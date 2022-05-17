package xyz.fumarase.killer.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import org.quartz.*;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import xyz.fumarase.killer.anlaiye.Manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author YuanTao
 */
@Component
@Data
@TableName(value = "job", autoResultMap = true)
@TypeAlias("Job")
public class JobModel extends Model {
    Long source;
    Long target;
    @TableField("shop_id")
    Integer shopId;
    @TableField(value = "black_list", typeHandler = JacksonTypeHandler.class)
    List<String> blackList;
    @TableField(value = "need_list", typeHandler = JacksonTypeHandler.class)
    HashMap<String, Integer> needList;
    Boolean enable;

    String hash;
    @TableId
    Integer id;

    Integer hour;
    Integer minute;
    Integer timeout;
    String info;

    public JobModel initHash() {
        this.hash = DigestUtils.md5DigestAsHex(this.toString().getBytes());
        return this;
    }


    @TableField("add_time")
    private String addTime;
    @TableField("update_time")
    private String updateTime;
}