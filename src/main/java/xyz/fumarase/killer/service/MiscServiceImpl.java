package xyz.fumarase.killer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.fumarase.killer.anlaiye.Client;
import xyz.fumarase.killer.anlaiye.Login;
import xyz.fumarase.killer.mapper.ConfigMapper;
import xyz.fumarase.killer.model.ConfigModel;

import java.util.HashMap;
import java.util.List;
@Service("MiscService")
public class MiscServiceImpl implements IMiscService {
    @Autowired
    private ConfigMapper configMapper;
    @Override
    public Boolean requestCaptcha(Long userId) {
        return Login.requestCaptcha(userId);
    }
    @Override
    public List<HashMap<String, Object>> getSchool(Integer schoolId) {
        return (new Client()).getSchool(schoolId);
    }

    @Override
    public ConfigModel getConfig(Integer id){
        return configMapper.selectById(id);
    };
}
