package xyz.fumarase.killer.service;

import xyz.fumarase.killer.object.Container;
import xyz.fumarase.killer.object.Good;

import java.util.HashMap;
import java.util.List;

/**
 * @author YuanTao
 */
public interface IMiscService {
    Boolean requestCaptcha(Long userId);

    Container getSchool(Integer schoolId);

    HashMap<String, Object> getInfo();

    List<Good> testNeedItem(Integer shopId, String needItemName);
}
