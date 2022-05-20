package xyz.fumarase.killer.anlaiye.object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import xyz.fumarase.killer.anlaiye.client.Client;
import xyz.fumarase.killer.anlaiye.client.exception.ClientException;
import xyz.fumarase.killer.anlaiye.client.exception.OrderTimeoutException;
import xyz.fumarase.killer.anlaiye.client.exception.TokenInvalidException;
import xyz.fumarase.killer.anlaiye.crypto.Phone;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author YuanTao
 */
@Setter
@Slf4j
public class User {
    @JsonIgnore
    private final Client client;
    @JsonIgnore
    private Address address;
    private final List<Address> addresses;
    @JsonIgnore
    private Shop shop;
    private final Long userId;
    @JsonIgnore
    private List<String> blackList;
    @JsonIgnore
    private HashMap<String, Integer> needList;
    private Boolean isTokenValid = true;
    @JsonIgnore
    private Integer timeout;
    @JsonIgnore
    private Long startTimeStamp;

    public User(Long userId, String token, String loginToken) {
        List<Address> addresses1;
        this.client = new Client(token, loginToken, 229);
        this.userId = userId;
        try {
            addresses1 = client.getAddress();
        } catch (TokenInvalidException e) {
            log.info("token失效");
            this.isTokenValid = false;
            addresses1 = new ArrayList<>(0);
        }

        addresses = addresses1;
    }


    public User setShop(Integer shopId) {
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
        setBlackList(blackList);
        return this;
    }

    public User need(HashMap<String, Integer> needList) {
        setNeedList(needList);
        return this;
    }

    public User waitForShop() throws OrderTimeoutException {
        startTimeStamp = System.currentTimeMillis();
        while (!shop.isOpen()) {
            if (System.currentTimeMillis() - startTimeStamp >= timeout * 1000) {
                throw new OrderTimeoutException();
            }
        }
        return this;
    }

    public Long run(Integer timeout) throws ClientException {
        //todo User应该对某些Client Exception进行处理，前端也应当对力所能及的exception进行支持
        //todo 比如，在TokenInvalid并且时间充足的条件下，在前端提示重置token
        //todo 当然，实现比较复杂。不能处理的，再抛出，写入运行历史数据库
        //long startTimeStamp = System.currentTimeMillis();
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
