package xyz.fumarase.killer.anlaiye.object;

import java.util.List;

/**
 * @author YuanTao
 */
public class OrderBuilder {
    private Address address;
    private List<OrderGood> orderGoods;
    private Shop shop;

    public static OrderBuilder newOrder() {
        return new OrderBuilder();
    }

    public OrderBuilder setAddress(Address address) {
        this.address = address;
        return this;
    }

    public OrderBuilder setOrderGoods(List<OrderGood> orderGoods) {
        this.orderGoods = orderGoods;
        return this;
    }

    public OrderBuilder setShop(Shop shop) {
        this.shop = shop;
        return this;
    }

    public Order build() {
        return new Order(shop, orderGoods, address);
    }
}
