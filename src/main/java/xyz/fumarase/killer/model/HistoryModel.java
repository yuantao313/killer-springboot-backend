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
@TableName("history")
public class HistoryModel extends Model {
    @TableId
    private String id;
    @TableField("job_id")
    private Integer jobId;
    private Boolean checked;
    private String datetime;
}
