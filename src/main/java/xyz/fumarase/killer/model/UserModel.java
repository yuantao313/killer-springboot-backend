package xyz.fumarase.killer.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;
import xyz.fumarase.killer.anlaiye.client.Client;
import xyz.fumarase.killer.anlaiye.client.exception.TokenInvalidException;
import xyz.fumarase.killer.anlaiye.crypto.Phone;
import xyz.fumarase.killer.anlaiye.object.Address;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author YuanTao
 */
@Data
@TableName(value = "user", autoResultMap = true)
@Accessors(chain = true)
public class UserModel extends ModelBase {
    private Integer id;

    @TableId
    @TableField("user_id")
    private Long userId;

    @TableField("login_token")
    private String loginToken;

    private String token;

    @TableField("add_time")
    private Date addTime;

    @TableField(value = "update_time", update = "now()")
    private Date updateTime;

    @TableField(exist = false)
    private List<Address> addressList;

    @TableField(exist = false)
    private boolean isTokenValid;

    @JsonIgnore
    public Address getAddress(Long target) {
        //global cache
        for (Address address : addressList) {
            if (address.mpDecryption().equals(target)) {
                return address;
            }
        }
        throw new RuntimeException("address not found");
    }

    @PostConstruct
    public void postConstruct() throws TokenInvalidException {
        Client client = new Client().setToken(token, loginToken);
        try {
            addressList = client.getAddress();
        } catch (TokenInvalidException e) {
            addressList = new ArrayList<>(0);
            throw e;
        }
    }
}
