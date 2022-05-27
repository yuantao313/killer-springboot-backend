package xyz.fumarase.killer.object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author YuanTao
 */
@Data
public class OrderGood {
    @JsonIgnore
    public static OrderGood EMPTY_ORDER_GOOD = new OrderGood();
    @JsonProperty("goods_sale_id")
    private final Long goodsSaleId;
    @JsonProperty("price")
    private final Double price;
    @JsonProperty("number")
    private Integer number;

    public OrderGood() {
        this.goodsSaleId = -1L;
        this.price = 0.0;
        this.number = 0;
    }

    public OrderGood(Long goodsSaleId, Double price, Integer number) {
        this.goodsSaleId = goodsSaleId;
        this.price = price;
        this.number = number;
    }
}
