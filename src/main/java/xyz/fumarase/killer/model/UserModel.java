package xyz.fumarase.killer.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;
import xyz.fumarase.killer.anlaiye.client.Client;
import xyz.fumarase.killer.anlaiye.client.exception.TokenInvalidException;
import xyz.fumarase.killer.anlaiye.object.Address;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author YuanTao
 */
@Data
@TableName(value = "user", autoResultMap = true)
@Accessors(chain = true)
public class UserModel extends ModelBase implements Serializable {
    private Integer id;

    @TableId
    @TableField("user_id")
    private Long userId;

    @TableField("login_token")
    @JsonIgnore
    private String loginToken;
    @JsonIgnore
    private String token;

    @TableField("add_time")
    private Date addTime;

    @TableField(value = "update_time", update = "now()")
    private Date updateTime;

    @TableField(exist = false)
    private boolean tokenValid;

    @TableField(exist = false)
    private List<Address> addressList;

    public UserModel afterLoad() {
        Client client = (new Client()).setToken(token, loginToken);
        try {
            addressList = client.getAddress();
            tokenValid = true;
        } catch (TokenInvalidException e) {
            addressList = new ArrayList<>();
            tokenValid = false;
        }
        return this;
    }

    public Address getAddress(Long target) {
        for (Address address : addressList) {
            if (address.mpDecryption().equals(target)) {
                return address;
            }
        }
        return null;
    }
}
