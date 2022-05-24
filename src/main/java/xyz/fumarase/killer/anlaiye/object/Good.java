package xyz.fumarase.killer.anlaiye.object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author YuanTao
 */
@Data
@Builder
public class Good {
    String name;
    @JsonIgnore
    Long goodId;
    Double price;
    Integer limit;
    String tag;

    Integer stock;

    public Boolean belongTo(String keyWord) {
        return getFullName().contains(keyWord);
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

    public OrderGood toOrder() {
        return new OrderGood(goodId, price, 1);
    }

    @JsonIgnore
    public String getFullName() {
        return (tag + "-" + name).replaceAll("\\s*", "");
    }
}
