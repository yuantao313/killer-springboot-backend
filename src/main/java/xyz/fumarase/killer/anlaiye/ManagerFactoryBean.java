package xyz.fumarase.killer.anlaiye;

import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.fumarase.killer.anlaiye.object.User;
import xyz.fumarase.killer.anlaiye.object.UserBuilder;
import xyz.fumarase.killer.mapper.JobMapper;
import xyz.fumarase.killer.mapper.UserMapper;
import xyz.fumarase.killer.model.JobModel;
import xyz.fumarase.killer.model.UserModel;

import java.util.HashMap;

/**
 * @author YuanTao
 */
@Component
public class ManagerFactoryBean implements FactoryBean<Manager> {

    private final static Logger logger = LoggerFactory.getLogger(ManagerFactoryBean.class);

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JobMapper jobMapper;

    @Override
    public Manager getObject() {
        Manager manager = new Manager();
        try {
            HashMap<Long, User> users = new HashMap<>();
            for (UserModel userModel : userMapper.selectList(null)) {
                User user = UserBuilder.newUser().fromModel(userModel).build();
                logger.info("从数据库装配用户：{}", userModel.getUserId());
                users.put(userModel.getUserId(), user);
            }
            logger.info("装配用户完成,共{}个用户", users.size());
            manager.setUsers(users);
            manager.setScheduler(new StdSchedulerFactory().getScheduler());
            for (JobModel jobModel : jobMapper.selectList(null)) {
                logger.info("从数据库装配任务：{}", jobModel);
                manager.addJob(jobModel);
            }
            logger.info("装配任务完成,共{}个任务", jobMapper.selectCount(null));
            manager.getScheduler().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return manager;
    }

    @Override
    public Class<?> getObjectType() {
        return Manager.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
