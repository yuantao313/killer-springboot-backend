package xyz.fumarase.killer.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author YuanTao
 */
@Data
@TableName("history")
public class HistoryModel extends BaseModel {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("job_id")
    private Integer jobId;
    private Boolean checked;
    private Date time;
    private String status;
    @TableField("is_manual")
    private Boolean isManual;
    @TableField("order_id")
    private Long orderId;
}
