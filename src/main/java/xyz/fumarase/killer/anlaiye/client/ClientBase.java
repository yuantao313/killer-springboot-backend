package xyz.fumarase.killer.anlaiye.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import xyz.fumarase.killer.anlaiye.client.exception.TokenInvalidException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

@Data
public abstract class ClientBase {
    protected int containerId = 110;
    protected OkHttpClient httpClient = new OkHttpClient();
    protected JsonMapper jsonMapper = new JsonMapper();

    protected String API_ROOT = "";

    public ClientBase(String API_ROOT, int containerId) {
        this.API_ROOT = API_ROOT;
        this.containerId = containerId;
    }
    /**
     * 发送get请求
     *
     * @param action 请求的action
     * @param data   请求的数据
     * @return 请求结果
     */
    /**
     * 发送get请求
     *
     * @param entrypoint 请求的entrypoint
     * @param data       请求的数据
     * @return 请求的结果
     */
    protected synchronized JsonNode get(String entrypoint, HashMap<String, Object> data) {
        Request.Builder rb = new Request.Builder();
        HttpUrl.Builder urlb = Objects.requireNonNull(HttpUrl.parse(API_ROOT + entrypoint)).newBuilder();
        for (String key : data.keySet()) {
            urlb.addQueryParameter(key, data.get(key).toString());
        }
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

    /**
     * 发送post请求
     *
     * @param data       请求的数据
     * @param entrypoint 请求的entrypoint
     * @return 请求结果
     */
    protected synchronized JsonNode post(String entrypoint, HashMap<String, Object> data) throws TokenInvalidException {
        JsonNode result = null;
        try {
            Request.Builder rb = new Request.Builder();
            HttpUrl.Builder urlb = Objects.requireNonNull(HttpUrl.parse(API_ROOT + entrypoint)).newBuilder();
            RequestBody body = RequestBody.create(jsonMapper.writeValueAsString(data), okhttp3.MediaType.parse("application/json; charset=utf-8"));
            Request request = rb.url(urlb.build()).post(body).build();
            result = jsonMapper.readTree(
                    Objects.requireNonNull(
                                    httpClient.newCall(request)
                                            .execute()
                                            .body())
                            .string());

        } catch (Exception e) {
            e.printStackTrace();
        }
        assert result != null;
        if (!result.get("result").asBoolean()) {
            if (result.get("flag").asInt() == -641) {
                throw new TokenInvalidException();
            }
        }
        return result;
    }

}
