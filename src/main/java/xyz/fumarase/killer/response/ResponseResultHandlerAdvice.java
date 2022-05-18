package xyz.fumarase.killer.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import reactor.util.annotation.Nullable;

/**
 * @author YuanTao
 */
@RestControllerAdvice
public class ResponseResultHandlerAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(@Nullable Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        JsonMapper jsonMapper = new JsonMapper();
        try {
            if (body instanceof String) {
                return ResponseResult.success(jsonMapper.writeValueAsString(body));
            } else if (body instanceof ResponseResult<?>) {
                return body;
            } else {
                return ResponseResult.success(body);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
