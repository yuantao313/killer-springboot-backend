package xyz.fumarase.killer.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.quartz.Job;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.List;

/**
 * @author YuanTao
 */
@Component
@Data
@TableName(value = "job", autoResultMap = true)
@TypeAlias("Job")
public class JobModel extends Model{
    Long source;
    Long target;
    @TableField("shop_id")
    Integer shopId;
    Integer hour;
    Integer minute;
    @TableField(value = "black_list", typeHandler = JacksonTypeHandler.class)
    List<String> blackList;
    @TableField(value = "need_list", typeHandler = JacksonTypeHandler.class)
    HashMap<String, Integer> needList;
    Boolean enable;
    Integer timeout;
    @TableId
    String hash;

    public JobModel initHash(){
        this.hash = DigestUtils.md5DigestAsHex(this.toString().getBytes());
        return this;
    }

}