package xyz.fumarase.killer.service;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.fumarase.killer.object.Container;
import xyz.fumarase.killer.object.Manager;
import xyz.fumarase.killer.anlaiye.client.Client;
import xyz.fumarase.killer.anlaiye.login.Login;
import xyz.fumarase.killer.object.Good;
import xyz.fumarase.killer.object.Shop;
import xyz.fumarase.killer.mapper.JobMapper;
import xyz.fumarase.killer.mapper.UserMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author YuanTao
 */
@Service("MiscService")
@NoArgsConstructor
public class MiscServiceImpl implements IMiscService {
    private JobMapper jobMapper;
    private UserMapper userMapper;

    private Manager manager;

    @Autowired
    public void setManager(Manager manager) {
        this.manager = manager;
    }

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
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
    public Container getSchool(Integer schoolId) {
        return (new Client()).getContainer(schoolId);
    }

    @Override
    public HashMap<String, Object> getInfo() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userNum", userMapper.selectCount(null));
        result.put("jobNum", jobMapper.selectCount(null));
        return result;
    }

    @Override
    public List<Good> testNeedItem(Integer shopId,String needItemName) {
        Shop shop = (new Client()).getShop(shopId);
        List<Good> goods = new ArrayList<>(shop.getGoods().values());
        goods.removeIf(good -> !good.belongTo(needItemName));
        return goods;
    }
}
