package xyz.fumarase.killer.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import xyz.fumarase.killer.constrant.HistoryStatusEnum;

import java.util.Date;

/**
 * @author YuanTao
 */
@Data
@TableName("history")
@Accessors(chain = true)
public class HistoryModel extends ModelBase {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("job_id")
    private Integer jobId;

    private Boolean checked;

    private Date time;

    private HistoryStatusEnum status;

    @TableField("is_manual")
    private Boolean isManual;

    @TableField("order_id")
    private Long orderId;
}
