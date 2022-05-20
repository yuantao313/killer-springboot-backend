package xyz.fumarase.killer.anlaiye;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;
import xyz.fumarase.killer.anlaiye.object.User;
import xyz.fumarase.killer.anlaiye.object.UserBuilder;
import xyz.fumarase.killer.mapper.HistoryMapper;
import xyz.fumarase.killer.mapper.JobMapper;
import xyz.fumarase.killer.mapper.UserMapper;
import xyz.fumarase.killer.model.JobModel;
import xyz.fumarase.killer.model.UserModel;

import java.util.HashMap;

/**
 * @author YuanTao
 */
@Component
@Slf4j
public class ManagerFactoryBean implements FactoryBean<Manager> {
    private UserMapper userMapper;

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    private JobMapper jobMapper;

    @Autowired
    public void setJobMapper(JobMapper jobMapper) {
        this.jobMapper = jobMapper;
    }

    private HistoryMapper historyMapper;

    @Autowired
    public void setHistoryMapper(HistoryMapper historyMapper) {
        this.historyMapper = historyMapper;
    }

    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    public void setSchedulerFactoryBean(SchedulerFactoryBean schedulerFactoryBean) {
        this.schedulerFactoryBean = schedulerFactoryBean;
    }

    @Override
    public Manager getObject() {
        Manager manager = new Manager();
        manager.setUserMapper(userMapper);
        manager.setJobMapper(jobMapper);
        manager.setHistoryMapper(historyMapper);
        manager.setScheduler(schedulerFactoryBean.getScheduler());
        try {
            HashMap<Long, User> users = new HashMap<>(userMapper.selectList(null).size());
            for (UserModel userModel : userMapper.selectList(null)) {
                log.info("从数据库装配用户：{}", userModel.getUserId());
                users.put(userModel.getUserId(), UserBuilder.newUser().fromModel(userModel).build());
            }
            log.info("装配用户完成,共{}个用户", users.size());
            manager.setUsers(users);
            for (JobModel jobModel : jobMapper.selectList(null)) {
                log.info("从数据库装配任务：{}", jobModel);
                manager.loadJob(jobModel);
            }
            log.info("装配任务完成,共{}个任务", jobMapper.selectCount(null));
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
