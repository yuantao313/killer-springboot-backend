package xyz.fumarase.killer.anlaiye.object;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author YuanTao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Precheck {
    private String deliveryDate;
    private String deliveryTime;
    private List<Long> invalidGoodId;


    public static Precheck getDefault(){
        return new Precheck((new SimpleDateFormat("yyyyMMdd")).format(new Date()),"0",new ArrayList<>());
    }
}
