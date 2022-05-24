package xyz.fumarase.killer.anlaiye.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import xyz.fumarase.killer.anlaiye.client.exception.ClientException;
import xyz.fumarase.killer.anlaiye.client.exception.TokenInvalidException;
import xyz.fumarase.killer.anlaiye.object.*;

import java.io.IOException;
import java.util.*;

/**
 * @author YuanTao
 */
@Getter
@Slf4j
public class Client implements IClient<Order> {
    private final OkHttpClient httpClient = new OkHttpClient();
    private final static JsonMapper jsonMapper = new JsonMapper();
    private final Integer schoolId;
    private String token;
    private String loginToken;
    private final static String API_ROOT = "https://web-agent.anlaiye.com/miniprogram";
    private final static String APP_VERSION = "8.1.3";

    public Client() {
        this(null, null, 110);
    }

    public Client(String token, String loginToken, Integer schoolId) {
        this.token = token;
        this.loginToken = loginToken;
        this.schoolId = schoolId;
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
    private HashMap<String, Object> wrap(HashMap<String, Object> data, Boolean anonymous) {
        data.put("app_version", APP_VERSION);
        data.put("time", System.currentTimeMillis());
        if (!anonymous && token != null && loginToken != null) {
            data.put("token", token);
            data.put("login_token", loginToken);
        }
        return data;
    }

    /**
     * 发送get请求
     *
     * @param action 请求的action
     * @param data   请求的数据
     * @return 请求结果
     */
    private synchronized JsonNode get(String action, HashMap<String, Object> data) {
        return get(action, "/agent/get", data, true);
    }

    /**
     * 发送get请求
     *
     * @param action     请求的action
     * @param entrypoint 请求的entrypoint
     * @param data       请求的数据
     * @param anonymous  是否匿名
     * @return 请求的结果
     */
    private synchronized JsonNode get(String action, String entrypoint, HashMap<String, Object> data, Boolean anonymous) {

        data = wrap(data, anonymous);
        Request.Builder rb = new Request.Builder();
        HttpUrl.Builder urlb = Objects.requireNonNull(HttpUrl.parse(API_ROOT + entrypoint)).newBuilder();
        for (String key : data.keySet()) {
            urlb.addQueryParameter(key, data.get(key).toString());
        }
        urlb.addQueryParameter("action", action);
        Request request = rb.url(urlb.build()).build();
        try {
            return jsonMapper.readTree(Objects.requireNonNull(httpClient.newCall(request).execute().body()).string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 发送post请求
     *
     * @param action 请求的action
     * @param data   请求的数据
     * @return 请求结果
     */
    private synchronized JsonNode post(String action, HashMap<String, Object> data) throws TokenInvalidException {
        return post(action, "/agent/post", data, false);
    }

    /**
     * 发送post请求
     *
     * @param action     请求的action
     * @param data       请求的数据
     * @param entrypoint 请求的entrypoint
     * @param anonymous  是否匿名
     * @return 请求结果
     */
    private synchronized JsonNode post(String action, String entrypoint, HashMap<String, Object> data, Boolean anonymous) throws TokenInvalidException {
        data = wrap(data, anonymous);
        data.put("action", action);
        JsonNode result = null;
        try {
            Request.Builder rb = new Request.Builder();
            HttpUrl.Builder urlb = Objects.requireNonNull(HttpUrl.parse(API_ROOT + entrypoint)).newBuilder();
            RequestBody body = RequestBody.create(jsonMapper.writeValueAsString(data), okhttp3.MediaType.parse("application/json; charset=utf-8"));
            Request request = rb.url(urlb.build()).post(body).build();
            result = jsonMapper.readTree(Objects.requireNonNull(httpClient.newCall(request).execute().body()).string());

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!result.get("result").asBoolean()) {
            if (result.get("flag").asInt() == -641) {
                throw new TokenInvalidException();
            }
        }
        return result;
    }

    /**
     * 获取学校中存在的商家和商家ID
     *
     * @param schoolId 学校ID
     * @return 学校中存在的商家和商家ID
     */
    public Container getContainer(int schoolId) {
        return null;
    }

    public List<HashMap<String, Object>> getSchool(Integer schoolId) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("school_id", schoolId);
        data.put("target", "merchants");
        data.put("page", 1);
        data.put("pageSize", 1000);
        JsonNode result = null;
        try {
            result = post("pub/shop/list", data);
        } catch (TokenInvalidException ignored) {

        }
        List<HashMap<String, Object>> shops = new ArrayList<>();
        for (JsonNode j : result.get("data")) {
            HashMap<String, Object> shop = new HashMap<>(2);
            shop.put("shop_id", j.get("id").asInt());
            shop.put("shop_name", j.get("shop_name").asText());
            shops.add(shop);
        }
        return shops;
    }

    public Shop getShop(int shopId) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("shop_id", shopId);
        data.put("target", "merchants");
        JsonNode shopNode = get("pub/shop/goodsV2", data).get("data");
        Shop shop = new Shop();
        shop.setShopId(shopId);
        shop.setShopName(shopNode.get("shop_detail").get("shop_name").asText());
        if (shopNode.get("shop_detail").get("is_self_take_new").asInt() == 1) {
            shop.setSelfTakeAddress(shopNode.get("shop_detail").get("self_take_address").asText());
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
        shop.setGoods(goods);
        return shop;
    }

    public boolean isShopOpen(int shopId) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("shop_id", shopId);
        data.put("target", "merchants");
        JsonNode shopNode = get("pub/shop/goodsV2", data).get("data");
        try {
            return true;
            //return shopNode.get("shop_detail").get("is_open").asInt() == 1;
        } catch (NullPointerException e) {
            return true;
        }
    }

    public List<Address> getAddress() throws TokenInvalidException {
        HashMap<String, Object> data = new HashMap<>();
        data.put("page", 1);
        data.put("pageSize", 1000);
        data.put("target", "passport_v3");
        JsonNode result = post("pub/address/list", data).get("data");
        try {
            return jsonMapper.readValue(result.toString(), new TypeReference<List<Address>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }



    /**
     * 按照precheck结果修改order对象为有效
     * @param shop shop对象，此参数会在之后进行解耦
     * @param order order对象
     * @return 有效的order对象
     * @throws ClientException
     */
    @SneakyThrows(InterruptedException.class)
    public Order precheck(Shop shop,Order order) throws ClientException {
        HashMap<String, Object> data = new HashMap<>();
        data.put("target", "order_center");
        data.put("school_id", schoolId);
        data.put("supplier_id", order.getSupplierId());
        data.put("supplier_short_name", shop.getShopName());//todo
        data.put("goods", order.getGoods());
        data.put("orderType", shop.isSelfTake() ? 1 : 0);//把manager的get shop 改成 全局缓存
        JsonNode jsonNode = null;
        try {
            //返回最早的时间
            while (true) {
                jsonNode = post("food/order/precheck", data);
                if (jsonNode.get("result").asBoolean()) break;
                else {
                    Thread.sleep(1000);
                }
            }
            JsonNode anode = jsonNode.get("data").get("deliveryDateTimeList").get(0);
            order.setDeliveryDate(anode.get("delivery_date").asText());
            order.setDeliveryTime(anode.get("delivery_TimeList").get(0).get("delivery_time").asText());
            for (JsonNode node : jsonNode.get("data").get("right_goods")) {
                if (node.get("status").asInt() != 0) {
                    /*
                     * 状态码说明
                     * 0->无异常
                     * 3000->已下架
                     * 3001->已售罄
                     * 3003->价格更新
                     * */
                    for(OrderGood orderGood: order.getGoods()){
                        if(orderGood.getGoodsSaleId().equals(node.get("good_id").asLong())){
                            order.getGoods().remove(orderGood);
                        }
                    }
                }
            }
            return order;
        } catch (NullPointerException e) {
            return order;
        }
    }

    @SneakyThrows(JsonProcessingException.class)
    public Long order(Order order) throws ClientException {
        HashMap<String, Object> data = jsonMapper.readValue(jsonMapper.writeValueAsString(order), new TypeReference<HashMap<String, Object>>() {
        });
        JsonNode orderNode = post("food/order/info", data);
        if (orderNode.get("result").asBoolean()) {
            return orderNode.get("data").get("orderId").asLong();
        } else {
            log.info("失败原因{}", orderNode.get("message").asText());
        }
        return -1L;
    }
}