package xyz.fumarase.killer.service;

import xyz.fumarase.killer.model.ConfigModel;

import java.util.HashMap;
import java.util.List;

/**
 * @author YuanTao
 */
public interface IMiscService {
    Boolean requestCaptcha(Long userId);

    List<HashMap<String, Object>> getSchool(Integer schoolId);

    ConfigModel getConfig(Integer id);

    HashMap<String, Object> getInfo();
}
