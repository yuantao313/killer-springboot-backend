package xyz.fumarase.killer.anlaiye;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.util.DigestUtils;
import xyz.fumarase.killer.anlaiye.crypto.GraphToken;
import xyz.fumarase.killer.anlaiye.crypto.Password;
import xyz.fumarase.killer.anlaiye.crypto.Phone;


import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author YuanTao
 */
public class Login {
    private static final JsonMapper jsonMapper = new JsonMapper();
    private static final OkHttpClient httpClient = new OkHttpClient();

    private static String sign(final String urlData, final HashMap<String, Object> data) throws JsonProcessingException {
        final String key = "pIWS1fRknjsgznoCyJHxdd8OL855Q2Kh";
        String dataString = jsonMapper.writeValueAsString(data);
        return DigestUtils.md5DigestAsHex(((urlData + dataString + key).getBytes(StandardCharsets.UTF_8)));
    }

    public static HashMap<String, String> loginWithPassword(Long userId, String password) throws Exception {
        return login(userId, password, null);
    }

    public static HashMap<String, String> loginWithCaptcha(Long userId, String captcha) throws Exception {
        return login(userId, "", captcha);
    }

    public static HashMap<String, String> login(Long userId, String password, String captcha) throws Exception {
        String userIdEncryption = Phone.encrypt(String.valueOf(userId)).toUpperCase();
        String passwordEncryption = !Objects.equals(password, "") ? Password.encrypt(password).toLowerCase() : "";
        HashMap<String, Object> data2 = new HashMap<>(5);
        data2.put("password", passwordEncryption);
        data2.put("school_id", 229);
        data2.put("mp_encryption", userIdEncryption);
        data2.put("type", "android");
        data2.put("captcha_type", 101);
        if (captcha != null) {
            data2.put("captcha", captcha);
        }
        HashMap<String, Object> data1 = new HashMap<>(5);
        data1.put("app_version", "8.1.9");
        data1.put("time", System.currentTimeMillis());
        data1.put("client_type", 2);
        data1.put("device_id", "alyMiniApp");//todo deviceIdGenerator
        data1.put("data", URLEncoder.encode(jsonMapper.writeValueAsString(data2), StandardCharsets.UTF_8));
        Request.Builder rb = new Request.Builder();
        RequestBody body = RequestBody.create(jsonMapper.writeValueAsString(data1), okhttp3.MediaType.parse("application/json; charset=utf-8"));
        rb.url("http://user-base.anlaiye.com/api/passport/auth");
        Request request = rb.post(body).build();
        JsonNode loginResp = jsonMapper.readTree(Objects.requireNonNull(httpClient.newCall(request).execute().body()).string());
        if (!loginResp.get("result").asBoolean()) {
            if (loginResp.get("flag").asInt() == -611) {
                System.out.println("invalid");
            } else if (loginResp.get("flag").asInt() == -616) {
                System.out.println("unsafe");
            }else if(loginResp.get("flag").asInt()==-1){
                System.out.println("timeout");
            }
            return null;
        } else {
            HashMap<String, String> result = new HashMap<>(2);
            result.put("login_token", loginResp.get("data").get("login_token").asText());
            result.put("token", loginResp.get("data").get("my_login_token").asText());
            return result;
        }
    }

    public static Boolean requestCaptcha(Long userId) {
        String userIdEncryption = Phone.encrypt(String.valueOf(userId)).toUpperCase();
        HashMap<String, Object> data = new HashMap<>(4);
        data.put("code_type", 1);
        data.put("mobile_encryption", userIdEncryption);
        data.put("type", 101);
        final String urlData = "appplt=aph&appver=8.1.9&appid=1";
        while (true) {
            try {
                String sign = sign(urlData, data);
                System.out.println(sign);
                Request.Builder rb = new Request.Builder();
                RequestBody body = RequestBody.create(jsonMapper.writeValueAsString(data), okhttp3.MediaType.parse("application/json; charset=utf-8"));
                rb.url("https://smsj.anlaiye.com.cn/smsj/v1/message/send_checkcode?" + urlData + "&sign=" + sign);
                Request request = rb.post(body).build();
                JsonNode result = jsonMapper.readTree(httpClient.newCall(request).execute().body().string());
                if (result.get("flag").asInt() == 1) {
                    if (result.get("data").has("graph_token")) {
                        String graphTokenDecryption = GraphToken.decryptToken(result.get("data").get("graph_token").asText());
                        Thread.sleep(1145, 14);
                        data.put("graph_token", graphTokenDecryption);
                    } else {
                        return true;
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
