package xyz.fumarase.killer.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author YuanTao
 */
@Component
@Data
@TableName(value = "job", autoResultMap = true)
@TypeAlias("Job")
public class JobModel extends BaseModel {
    Long source;
    Long target;
    @TableField("shop_id")
    Integer shopId;
    @TableField(value = "black_list", typeHandler = JacksonTypeHandler.class)
    List<String> blackList;
    @TableField(value = "need_list", typeHandler = JacksonTypeHandler.class)
    HashMap<String, Integer> needList;
    Boolean enable;
    @TableId(type = IdType.AUTO)
    Integer id;
    Integer hour;
    Integer minute;
    Integer timeout;
    String info;
    @TableField("add_time")
    private Date addTime;
    @TableField("update_time")
    private Date updateTime;
    @TableField(exist = false)
    private Date nextRunTime;
}