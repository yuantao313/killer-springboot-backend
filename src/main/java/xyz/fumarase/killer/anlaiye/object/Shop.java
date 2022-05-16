package xyz.fumarase.killer.anlaiye.object;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.fumarase.killer.anlaiye.Client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
public class Shop {
    private final static Logger logger = LoggerFactory.getLogger(Shop.class);

    private static final Client client = new Client();
    private Integer shopId;
    private String shopName;
    private String selfTakeAddress;

    private HashMap<Long, Good> goods = new HashMap<>();

    public Shop() {
        this.shopId = null;
        this.shopName = null;
        this.selfTakeAddress = null;
    }

    public Boolean isOpen() {
        //todo cache
        return true;
    }

    public Boolean isSelfTake() {
        return this.selfTakeAddress != null;
    }

    public List<OrderGood> order(final List<String> blackList, final HashMap<String, Integer> needList) {
        int size = 0;
        for (Integer number : needList.values()) {
            size += number;
        }
        List<Good> goods = new ArrayList<>();
        //step1
        logger.info("开始按照黑名单排除");
        logger.info("黑名单: {}", blackList);
        for (Long goodId : this.goods.keySet()) {
            Good good = this.goods.get(goodId);
            String goodBlackWord = good.belongTo(blackList);
            if (goodBlackWord != null) {
                logger.info("{} 被排除,关键词：{}", good.getName(), goodBlackWord);
            } else {
                logger.info("{} 被添加到待选列表", good.getName());
                goods.add(good);
            }
        }
        logger.info("共计{}种商品", goods.size());
        //step2
        logger.info("开始按照需求添加");
        logger.info("需求: {}", needList);
        HashMap<Long, OrderGood> shoppingCart = new HashMap<>(size);
        HashMap<String, Integer> myNeedList = new HashMap<>(needList.size());
        boolean added;
        boolean limitA;
        boolean limitB;
        do {
            added = false;
            for (Good good : goods) {
                Long goodId = good.getGoodId();
                String keyWord = good.belongTo(new ArrayList<>(needList.keySet()));
                if (keyWord != null) {
                    limitA = true;
                    limitB = true;
                    logger.info("准备添加{},关键词:{}", good.getName(), keyWord);
                    if (myNeedList.getOrDefault(keyWord, 0) >= needList.get(keyWord)) {
                        limitA = false;
                    }
                    if (good.hasLimit() && shoppingCart.getOrDefault(goodId, OrderGood.EMPTY_ORDER_GOOD).getNumber() >= good.getLimit()) {
                        logger.info("{}已达到限购上限", good.getName());
                        limitB = false;
                    }
                    if (limitA && limitB) {
                        added=true;
                        myNeedList.put(keyWord, myNeedList.getOrDefault(keyWord, 0) + 1);
                        if (shoppingCart.containsKey(goodId)) {
                            shoppingCart.get(goodId).setNumber(shoppingCart.get(goodId).getNumber() + 1);
                        } else {
                            shoppingCart.put(goodId, good.toOrder());
                        }
                        logger.info("已添加{}:{}/{}", keyWord, myNeedList.get(keyWord), needList.get(keyWord));
                    }
                }
            }
        }
        while (added);
        logger.info("购物车总计{}件商品", shoppingCart.size());
        return new ArrayList<>(shoppingCart.values());
    }
}
