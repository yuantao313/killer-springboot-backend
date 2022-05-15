package xyz.fumarase.killer.anlaiye.object;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.List;

/**
 * @author YuanTao
 */
@Data
public class Good {
    String name;
    Long goodId;
    Double price;
    Integer limit;
    String tag;

    public Good(JsonNode goodNode) {
        name = goodNode.get("goods_name").asText();
        price = goodNode.get("sku_list").get(0).get("activity_price").asDouble();
        tag = goodNode.get("tag_name").asText();
        limit = goodNode.get("restriction_num").asInt();
        goodId = goodNode.get("sku_list").get(0).get("sku_id").asLong();
    }

    public Good(String name, Long goodId, Double price, Integer limit, String tag) {
        this.name = name;
        this.goodId = goodId;
        this.price = price;
        this.limit = limit;
        this.tag = tag;
    }

    public String belongTo(List<String> keyWords) {
        for (String keyWord : keyWords) {
            if (name.contains(keyWord) || tag.contains(keyWord)) {
                return keyWord;
            }
        }
        return null;
    }

    public Boolean hasLimit() {

        return limit != null && limit > 0;
    }

    public OrderGood toOrder(Integer amount) throws Exception {
        if (amount <= limit && amount > 0) {
            OrderGood orderGood = toOrder();
            orderGood.setNumber(amount);
            return orderGood;
        } else {
            throw new Exception("超出限制");
        }
    }

    public OrderGood toOrder() {
        return new OrderGood(goodId, price, 1);
    }

    public String getFullName() {
        return tag + "-" + name;
    }
}
