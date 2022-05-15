package xyz.fumarase.killer.anlaiye.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author YuanTao
 */
@Data
public class OrderGood {
    @JsonProperty("goods_sale_id")
    private final Long goodsSaleId;
    @JsonProperty("price")
    private final Double price;
    @JsonProperty("number")
    private Integer number;
    public OrderGood(Long goodsSaleId, Double price, Integer number) {
        this.goodsSaleId = goodsSaleId;
        this.price = price;
        this.number = number;
    }
}
