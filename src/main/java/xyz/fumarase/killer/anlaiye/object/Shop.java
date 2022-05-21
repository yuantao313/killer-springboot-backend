package xyz.fumarase.killer.anlaiye.object;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import xyz.fumarase.killer.anlaiye.client.Client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author YuanTao
 */
@Data
@Slf4j
public class Shop {
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
        return client.isShopOpen(this.shopId);
    }

    public Boolean isSelfTake() {
        return this.selfTakeAddress != null;
    }

    public HashMap<Long,OrderGood> order(final List<String> blackList, final HashMap<String, Integer> needList) {
        int size = 0;
        for (Integer number : needList.values()) {
            size += number;
        }
        List<Good> goods = new ArrayList<>();
        //step1
        log.info("开始按照黑名单排除");
        log.info("黑名单: {}", blackList);
        for (Long goodId : this.goods.keySet()) {
            Good good = this.goods.get(goodId);
            String goodBlackWord = good.belongTo(blackList);
            if (goodBlackWord != null) {
                log.info("{} 被排除,关键词：{}", good.getName(), goodBlackWord);
            } else {
                log.info("{} 被添加到待选列表", good.getName());
                goods.add(good);
            }
        }
        log.info("共计{}种商品", goods.size());
        //step2
        log.info("开始按照需求添加");
        log.info("需求: {}", needList);
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
                    log.info("准备添加{},关键词:{}", good.getName(), keyWord);
                    if (myNeedList.getOrDefault(keyWord, 0) >= needList.get(keyWord)) {
                        limitA = false;
                    }
                    if (good.hasLimit() && shoppingCart.getOrDefault(goodId, OrderGood.EMPTY_ORDER_GOOD).getNumber() >= good.getLimit()) {
                        log.info("{}已达到限购上限", good.getName());
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
                        log.info("已添加{}:{}/{}", keyWord, myNeedList.get(keyWord), needList.get(keyWord));
                    }
                }
            }
        }
        while (added);
        log.info("购物车总计{}件商品", shoppingCart.size());
        return shoppingCart;
    }
}
