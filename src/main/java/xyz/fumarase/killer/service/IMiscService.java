package xyz.fumarase.killer.service;

import xyz.fumarase.killer.model.ConfigModel;

import java.util.HashMap;
import java.util.List;

public interface IMiscService {
    public Boolean requestCaptcha(Long userId);

    List<HashMap<String, Object>> getSchool(Integer schoolId);

    ConfigModel getConfig(Integer id);
}
