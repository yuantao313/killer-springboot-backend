package xyz.fumarase.killer.anlaiye.object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import xyz.fumarase.killer.anlaiye.client.Client;
import xyz.fumarase.killer.anlaiye.client.exception.ClientException;
import xyz.fumarase.killer.anlaiye.client.exception.TokenInvalidException;
import xyz.fumarase.killer.anlaiye.crypto.Phone;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YuanTao
 */
@Slf4j
@Builder
@AllArgsConstructor
@Accessors(chain = true)
@ToString
@Data
public class User {
    @JsonIgnore
    private Client client;
    private List<Address> addresses;
    private Long userId;
    @Builder.Default
    private Boolean isTokenValid = true;

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

    public Address getAddress(Long target) {
        for (Address address : addresses) {
            if (Long.valueOf(Phone.decrypt(address.mpEncryption)).equals(target)) {
                return address;
            }
        }
        return null;
    }

    public Precheck precheck(Shop shop, List<OrderGood> orderGoods) throws ClientException {
        return client.precheck(shop, orderGoods);
    }

    public Long order(Order order) throws ClientException {
        return client.order(order);
    }
}
