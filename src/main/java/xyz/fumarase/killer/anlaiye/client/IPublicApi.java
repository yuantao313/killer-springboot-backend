package xyz.fumarase.killer.anlaiye.client;

import xyz.fumarase.killer.anlaiye.object.Shop;

import java.util.HashMap;
import java.util.List;

/**
 * @author YuanTao
 */
public interface IPublicApi {
    Shop getShop(Integer shopId);
    List<HashMap<String,Object>> getSchool(Integer schoolId);
}
