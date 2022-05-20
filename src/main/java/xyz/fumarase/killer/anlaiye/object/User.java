package xyz.fumarase.killer.anlaiye.object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import xyz.fumarase.killer.anlaiye.client.Client;
import xyz.fumarase.killer.anlaiye.client.exception.ClientException;
import xyz.fumarase.killer.anlaiye.client.exception.OrderTimeoutException;
import xyz.fumarase.killer.anlaiye.client.exception.TokenInvalidException;
import xyz.fumarase.killer.anlaiye.crypto.Phone;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author YuanTao
 */
@Data
@Slf4j
@Builder
@AllArgsConstructor
@Accessors(chain = true)
public class User {
    @JsonIgnore
    private final Client client;
    @JsonIgnore
    private Address address;
    private List<Address> addresses;
    @JsonIgnore
    private Shop shop;
    private final Long userId;
    @JsonIgnore
    private List<String> blackList;
    @JsonIgnore
    private HashMap<String, Integer> needList;
    @Builder.Default
    private Boolean isTokenValid = true;
    @JsonIgnore
    private Integer timeout;
    @JsonIgnore
    private Long startTimeStamp;

    public User initAddress() {
        try {
            addresses = client.getAddress();
        } catch (TokenInvalidException e) {
            log.info("token失效");
            this.isTokenValid = false;
            addresses = new ArrayList<>(0);
        }
        return this;
    }

    public User setShopId(Integer shopId) {
        shop = client.getShop(shopId);
        return this;
    }

    public User setTarget(Long target) {
        for (Address address : addresses) {
            if (Long.valueOf(Phone.decrypt(address.mpEncryption)).equals(target)) {
                this.address = address;
            }
        }
        return this;
    }

    public User avoid(List<String> blackList) {
        return setBlackList(blackList);
    }

    public User need(HashMap<String, Integer> needList) {
        return setNeedList(needList);
    }

    public User waitForShop() throws OrderTimeoutException {
        startTimeStamp = System.currentTimeMillis();
        while (!shop.isOpen()) {
            log.info("店铺未营业，1秒后重试");
            if (System.currentTimeMillis() - startTimeStamp >= timeout * 1000) {
                throw new OrderTimeoutException();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info("店铺营业");
        return this;
    }

    public Long run() throws ClientException {
        //todo User应该对某些Client Exception进行处理，前端也应当对力所能及的exception进行支持
        //todo 比如，在TokenInvalid并且时间充足的条件下，在前端提示重置token
        //todo 当然，实现比较复杂。不能处理的，再抛出，写入运行历史数据库
        List<OrderGood> orderGoods = shop.order(blackList, needList);
        String delivery = client.precheck(shop, orderGoods);
        Order order = OrderBuilder.newOrder()
                .setAddress(address)
                .setShop(shop)
                .setOrderGoods(orderGoods)
                .setDelivery((new SimpleDateFormat("yyyyMMdd")).format(new Date()), delivery)
                .build();
        Long orderId;
        do {
            orderId = client.order(order);
            if (orderId == -1L) {
                log.info("抢购失败,1s后重试");
            } else {
                log.info("订单号：" + orderId);
                return orderId;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (System.currentTimeMillis() - startTimeStamp < timeout * 1000);
        throw new OrderTimeoutException();
    }
}
