package xyz.fumarase.killer.anlaiye.object;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author YuanTao
 */
@Data
@AllArgsConstructor
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


    public String belongTo(List<String> keyWords) {
        for (String keyWord : keyWords) {
            List<String> splitKeyWords = List.of(keyWord.split("\\|"));
            for (String splitKeyWord : splitKeyWords) {
                if (getFullName().contains(splitKeyWord)) {
                    return keyWord;
                }
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
        return (tag + "-" + name).replaceAll("\\s*", "");
    }
}
