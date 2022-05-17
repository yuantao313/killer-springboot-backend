package xyz.fumarase.killer.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.fumarase.killer.anlaiye.Client;
import xyz.fumarase.killer.anlaiye.Login;
import xyz.fumarase.killer.mapper.ConfigMapper;
import xyz.fumarase.killer.mapper.JobMapper;
import xyz.fumarase.killer.mapper.UserMapper;
import xyz.fumarase.killer.model.ConfigModel;

import java.util.HashMap;
import java.util.List;

/**
 * @author YuanTao
 */
@Service("MiscService")
@NoArgsConstructor
@AllArgsConstructor
public class MiscServiceImpl implements IMiscService {
    private ConfigMapper configMapper;
    private JobMapper jobMapper;
    private UserMapper userMapper;

    @Autowired
    public void setConfigMapper(ConfigMapper configMapper) {
        this.configMapper = configMapper;
    }
    @Autowired

    public void setUserMapper(UserMapper userMapper){
        this.userMapper = userMapper;
    }
    @Autowired
    public void setJobMapper(JobMapper jobMapper) {
        this.jobMapper = jobMapper;
    }

    @Override
    public Boolean requestCaptcha(Long userId) {
        return Login.requestCaptcha(userId);
    }

    @Override
    public List<HashMap<String, Object>> getSchool(Integer schoolId) {
        return (new Client()).getSchool(schoolId);
    }

    @Override
    public ConfigModel getConfig(Integer id) {
        return configMapper.selectById(id);
    }

    @Override
    public HashMap<String,Object> getInfo(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("userNum",userMapper.selectCount(null));
        result.put("jobNum",jobMapper.selectCount(null));
        return result;
    }
}
