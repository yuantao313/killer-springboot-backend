package xyz.fumarase.killer.anlaiye;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.Getter;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import xyz.fumarase.killer.anlaiye.object.*;

import java.io.IOException;
import java.util.*;

/**
 * @author YuanTao
 */
@Getter
public class Client implements IClient {
    private final OkHttpClient httpClient = new OkHttpClient();
    private final static JsonMapper jsonMapper = new JsonMapper();
    private final Integer schoolId;
    private String token;
    private String loginToken;
    private final static String apiRoot = "https://web-agent.anlaiye.com/miniprogram";
    private final static String appVersion = "8.1.3";

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
     * @param token token
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

        data.put("app_version", appVersion);
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
        HttpUrl.Builder urlb = Objects.requireNonNull(HttpUrl.parse(apiRoot + entrypoint)).newBuilder();
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
    private synchronized JsonNode post(String action, HashMap<String, Object> data) {

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
    private synchronized JsonNode post(String action, String entrypoint, HashMap<String, Object> data, Boolean anonymous) {

        data = wrap(data, anonymous);
        data.put("action", action);
        try {
            Request.Builder rb = new Request.Builder();
            HttpUrl.Builder urlb = Objects.requireNonNull(HttpUrl.parse(apiRoot + entrypoint)).newBuilder();
            RequestBody body = RequestBody.create(jsonMapper.writeValueAsString(data), okhttp3.MediaType.parse("application/json; charset=utf-8"));
            Request request = rb.url(urlb.build()).post(body).build();
            return jsonMapper.readTree(Objects.requireNonNull(httpClient.newCall(request).execute().body()).string());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取学校中存在的商家和商家ID
     *
     * @param schoolId 学校ID
     * @return 学校中存在的商家和商家ID
     */
    public List<HashMap<String, Object>> getSchool(Integer schoolId) {

        HashMap<String, Object> data = new HashMap<>();
        data.put("school_id", schoolId);
        data.put("target", "merchants");
        data.put("page", 1);
        data.put("pageSize", 1000);
        JsonNode result = post("pub/shop/list", data);
        List<HashMap<String, Object>> shops = new ArrayList<>();
        for (JsonNode j : result.get("data")) {
            HashMap<String, Object> shop = new HashMap<>(2);
            shop.put("shop_id", j.get("id").asInt());
            shop.put("shop_name", j.get("shop_name").asText());
            shops.add(shop);
        }
        return shops;
    }

    public Shop getShop(Integer shopId) {
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
                Good newGood = new Good(goodNode);
                goods.put(newGood.getGoodId(), newGood);
            }
        }
        shop.setGoods(goods);
        return shop;
    }

    public List<Address> getAddress() {
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

    public String precheck(Shop shop, List<OrderGood> orderGoods) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("target", "order_center");
        data.put("school_id", schoolId);
        data.put("supplier_id", shop.getShopId());
        data.put("supplier_short_name", shop.getShopName());
        data.put("goods", orderGoods);
        data.put("orderType", shop.isSelfTake() ? 1 : 0);
        JsonNode jsonNode = post("pub/order/precheck", data);
        HashMap<String, String> result = new HashMap<>();
        try {
            return jsonNode.get("data").get("deliveryDateTimeList").get(0).get("delivery_TimeList").get(0).get("delivery_time").asText();
        } catch (Exception e) {
            return "0";
        }
    }

    public String order(Order order) {
        try {
            if (!order.getGoods().isEmpty()) {
                while (true) {
                    JsonNode orderNode = post("food/order/info", jsonMapper.readValue(jsonMapper.writeValueAsString(order), new TypeReference<HashMap<String, Object>>() {
                    }));
                    if (orderNode.get("result").asBoolean()) {
                        return orderNode.get("data").get("orderId").asText();
                    }else{
                        Thread.sleep(1000);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}