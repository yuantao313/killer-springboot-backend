package xyz.fumarase.killer.anlaiye.object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.fumarase.killer.anlaiye.Client;
import xyz.fumarase.killer.anlaiye.crypto.Phone;
import xyz.fumarase.killer.model.UserModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author YuanTao
 */
@Data
public class User implements IToModel{
    @JsonIgnore
    private final static Logger logger = LoggerFactory.getLogger(User.class);
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
    private HashMap<String,Integer> needList;

    public User(Long userId, String token, String loginToken) {
        this.client = new Client(token, loginToken, 229);
        this.userId = userId;
        this.addresses = client.getAddress();
    }

    public User waitForShop() {
        while (true) {
            try {
                if (shop.isOpen()) {
                    return this;
                } else {
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        this.blackList = blackList;
        return this;
    }
    public User need(HashMap<String,Integer> needList) {
        this.needList = needList;
        return this;
    }
    @Override
    public UserModel toModel() {
        UserModel userModel = new UserModel();
        userModel.setUserId(userId);
        userModel.setToken(client.getToken());
        userModel.setLoginToken(client.getLoginToken());
        return userModel;
    }

    public void run() {
        List<OrderGood> orderGoods = shop.order(blackList,needList);
        String delivery = client.precheck(shop, orderGoods);
        Order order = OrderBuilder.newOrder()
                .setAddress(address)
                .setShop(shop)
                .setOrderGoods(orderGoods)
                .setDelivery((new SimpleDateFormat("yyyyMMdd")).format(new Date()),delivery)
                .build();
        String orderId = client.order(order);
        logger.info("订单号：" + orderId);
    }

}
