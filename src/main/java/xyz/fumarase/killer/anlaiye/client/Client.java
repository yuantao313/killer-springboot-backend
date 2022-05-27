package xyz.fumarase.killer.anlaiye.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import xyz.fumarase.killer.anlaiye.client.exception.ClientException;
import xyz.fumarase.killer.anlaiye.client.exception.TokenInvalidException;
import xyz.fumarase.killer.object.job.exception.EmptyOrderException;
import xyz.fumarase.killer.anlaiye.object.*;
import xyz.fumarase.killer.object.*;

import java.util.*;

/**
 * @author YuanTao
 */
@Getter
@Slf4j

public class Client extends ClientBase implements IClient<Order> {
    private String token;
    private String loginToken;
    private final static String APP_VERSION = "8.1.3";

    public Client() {
        this("https://web-agent.anlaiye.com/miniprogram/agent/", 229);
    }

    public Client(String API_ROOT, int containerId) {
        super(API_ROOT, containerId);
    }


    /**
     * 设定token
     *
     * @param token      token
     * @param loginToken loginToken
     * @return 当前实例
     */
    public Client setToken(String token, String loginToken) {
        this.token = token;
        this.loginToken = loginToken;
        return this;
    }

    /**
     * 将请求数据包装上一些共性的参数
     *
     * @param data      请求数据
     * @param anonymous 是否匿名
     * @return 包装后的请求数据
     */
    protected HashMap<String, Object> wrap(HashMap<String, Object> data, boolean anonymous) {
        data.put("app_version", APP_VERSION);
        data.put("time", System.currentTimeMillis());
        if (!anonymous && token != null && loginToken != null) {
            data.put("token", token);
            data.put("login_token", loginToken);
        }
        return data;
    }

    private synchronized JsonNode get(String action, HashMap<String, Object> data, boolean anonymous) throws TokenInvalidException {
        data.put("action", action);
        return get("get", wrap(data, anonymous));
    }

    private synchronized JsonNode post(String action, HashMap<String, Object> data, boolean anonymous) throws TokenInvalidException {
        data.put("action", action);
        return post("post", wrap(data, anonymous));
    }

    /**
     * 获取学校中存在的商家和商家ID
     *
     * @param schoolId 学校ID
     * @return 学校中存在的商家和商家ID
     */
    @SneakyThrows(TokenInvalidException.class)
    @Cacheable(value = "container")
    public Container getContainer(int schoolId) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("school_id", containerId);
        data.put("target", "merchants");
        data.put("page", 1);
        data.put("pageSize", 1000);
        JsonNode result;
        result = post("pub/shop/list", data, true).get("data");
        Container.ContainerBuilder cb = Container.builder()
                .containerId(containerId);
        List<HashMap<String, Object>> shops = new ArrayList<>(0);
        assert result != null;
        for (JsonNode j : result) {
            HashMap<String, Object> shop = new HashMap<>(2);
            shop.put("shopId", j.get("id").asInt());
            shop.put("shopName", j.get("shop_name").asText());
            shops.add(shop);
        }
        return cb.shops(shops).build();
    }

    @SneakyThrows(TokenInvalidException.class)
    @Cacheable(value = "shop", key = "#shopId")
    public Shop getShop(int shopId) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("shop_id", shopId);
        data.put("target", "merchants");
        JsonNode shopNode = get("pub/shop/goodsV2", data, true).get("data");
        Shop.ShopBuilder sb = Shop.builder()
                .shopId(shopId)
                .shopName(shopNode.get("shop_detail").get("shop_name").asText());
        if (shopNode.get("shop_detail").get("is_self_take_new").asInt() == 1) {
            sb.selfTakeAddress(shopNode.get("shop_detail").get("self_take_address").asText());
        }
        HashMap<Long, Good> goods = new HashMap<>();
        for (JsonNode tagNode : shopNode.get("item_list")) {
            for (JsonNode goodNode : tagNode.get("goods_list")) {
                long goodId = goodNode.get("sku_list").get(0).get("sku_id").asLong();
                goods.put(goodId, Good.builder()
                        .goodId(goodId)
                        .name(goodNode.get("goods_name").asText())
                        .price(goodNode.get("sku_list").get(0).get("activity_price").asDouble())
                        .tag(goodNode.get("tag_name").asText())
                        .limit(goodNode.get("restriction_num").asInt())
                        .stock(goodNode.get("sku_list").get(0).get("stock").asInt())
                        .build());
            }
        }
        sb.goods(goods);
        return sb.build();
    }

    @SneakyThrows(TokenInvalidException.class)
    public boolean isShopOpen(int shopId) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("shop_id", shopId);
        data.put("target", "merchants");
        JsonNode shopNode = get("pub/shop/goodsV2", data, true).get("data");
        try {
            return shopNode.get("shop_detail").get("is_open").asInt() == 1;
        } catch (NullPointerException e) {
            return true;
        }
    }

    @Cacheable(value = "address", key = "#root.target.getToken()")
    public List<Address> getAddress() throws TokenInvalidException {
        HashMap<String, Object> data = new HashMap<>();
        data.put("page", 1);
        data.put("pageSize", 1000);
        data.put("target", "passport_v3");
        JsonNode result = post("pub/address/list", data, false).get("data");
        try {
            return jsonMapper.readValue(result.toString(), new TypeReference<List<Address>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 按照precheck结果生成有效Order对象
     *
     * @param shop       shop对象，此参数会在之后进行解耦
     * @param orderGoods order对象
     * @return 有效的order对象
     * @throws ClientException
     */
    @SneakyThrows(InterruptedException.class)
    public Order precheck(Shop shop, List<OrderGood> orderGoods, Address address, boolean skipGoodsCheck) throws ClientException, EmptyOrderException {
        HashMap<String, Object> data = new HashMap<>();
        data.put("target", "order_center");
        data.put("school_id", containerId);
        data.put("supplier_id", shop.getShopId());
        data.put("supplier_short_name", shop.getShopName());
        data.put("goods", orderGoods);
        data.put("orderType", shop.isSelfTake() ? 1 : 0);
        JsonNode jsonNode;
        Order order = null;
        try {
            while (true) {
                jsonNode = post("food/order/precheck", data, false);
                if (jsonNode.get("result").asBoolean()) break;
                else {
                    Thread.sleep(1000);
                }
            }
            order = OrderBuilder.newOrder()
                    .setOrderGoods(orderGoods)
                    .setAddress(address)
                    .setShop(shop)
                    .build();
            JsonNode anode = jsonNode.get("data").get("deliveryDateTimeList").get(0);
            order.setDeliveryDate(anode.get("delivery_date").asText());
            order.setDeliveryTime(anode.get("delivery_TimeList").get(0).get("delivery_time").asText());
            if (!skipGoodsCheck) {
                for (JsonNode node : jsonNode.get("data").get("right_goods")) {
                    if (node.get("status").asInt() != 0) {
                        for (OrderGood orderGood : orderGoods) {
                            if (orderGood.getGoodsSaleId().equals(node.get("goods_sale_id").asLong())) {
                                order.getGoods().remove(orderGood);
                                switch (node.get("status").asInt()) {
                                    case 3000:
                                        log.info("商品已下架");
                                        break;
                                    case 3001:
                                        log.info("商品已售罄");
                                        break;
                                    case 3003:
                                        log.info("商品价格更新");
                                        break;
                                }
                                break;
                            }
                        }
                    }
                }
            }
            if (order.getGoods().size() == 0) {
                throw new EmptyOrderException();
            }
            order.setGoods(orderGoods);
            return order;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return order;
        }
    }

    @SneakyThrows(JsonProcessingException.class)
    public Long order(Order order) throws ClientException {
        HashMap<String, Object> data = jsonMapper.readValue(jsonMapper.writeValueAsString(order), new TypeReference<HashMap<String, Object>>() {
        });
        JsonNode orderNode = post("food/order/info", data, false);
        if (orderNode.get("result").asBoolean()) {
            return orderNode.get("data").get("orderId").asLong();
        } else {
            log.info("失败原因{}", orderNode.get("message").asText());
        }
        return -1L;
    }
}