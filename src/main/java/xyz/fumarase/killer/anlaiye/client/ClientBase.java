package xyz.fumarase.killer.anlaiye.client;

import xyz.fumarase.killer.anlaiye.client.exception.TokenInvalidException;
import xyz.fumarase.killer.anlaiye.object.AddressBase;
import xyz.fumarase.killer.anlaiye.object.Shop;

import java.util.HashMap;
import java.util.List;

/**
 * @author YuanTao
 */
public abstract class ClientBase {
    public Shop getShop() {
        return null;
    }

    public List<HashMap<String, Object>> getSchool() {
        return null;
    }

    public List<? extends AddressBase> getAddress() throws TokenInvalidException {
        return null;
    }

    public HashMap<String, Object> precheck() throws TokenInvalidException {
        return null;
    }

    public Long order() throws TokenInvalidException {
        return null;
    }
}
