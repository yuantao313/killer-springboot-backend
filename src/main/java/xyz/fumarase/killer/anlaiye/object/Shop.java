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
        boolean added;
        do {
            added = false;
            for (Good good : goods) {
                Long goodId = good.getGoodId();
                String keyWord = good.belongTo(new ArrayList<>(needList.keySet()));
                if (keyWord != null) {
                    logger.info("准备添加{},关键词:{}", good.getName(), keyWord);
                    if (shoppingCart.containsKey(goodId)) {
                        OrderGood orderGood = shoppingCart.get(goodId);
                        if (good.hasLimit()) {
                            if (orderGood.getNumber() < needList.get(keyWord)) {
                                orderGood.setNumber(orderGood.getNumber() + 1);
                                added = true;
                                logger.info("已添加:{}/{}", orderGood.getNumber(), needList.get(keyWord));
                                //todo bug:应该copy needlist 然后相减
                            } else {
                                logger.info("达到上限");
                            }
                        }
                    } else {
                        OrderGood orderGood = good.toOrder();
                        shoppingCart.put(goodId, orderGood);
                        logger.info("已添加:{}/{}", orderGood.getNumber(), needList.get(keyWord));
                        added = true;
                    }
                }
            }
        } while (added);
        logger.info("购物车总计{}件商品", shoppingCart.size());
        return new ArrayList<>(shoppingCart.values());
    }
}
