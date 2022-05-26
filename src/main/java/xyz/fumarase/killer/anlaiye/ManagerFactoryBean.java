package xyz.fumarase.killer.anlaiye;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;
import xyz.fumarase.killer.anlaiye.client.Client;
import xyz.fumarase.killer.reporter.Reporter;
import xyz.fumarase.killer.mapper.HistoryMapper;
import xyz.fumarase.killer.mapper.JobMapper;
import xyz.fumarase.killer.mapper.UserMapper;
import xyz.fumarase.killer.model.JobModel;

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

    private Reporter reporter;
    @Autowired
    public  void setReporter(Reporter reporter){
        this.reporter = reporter;
    }


    @Override
    public Manager getObject() {
        log.info("Manager开始初始化");
        Manager manager = new Manager();
        manager.setUserMapper(userMapper);
        manager.setJobMapper(jobMapper);
        manager.setHistoryMapper(historyMapper);
        manager.setScheduler(schedulerFactoryBean.getScheduler());
        manager.setClient(new Client());
        manager.setReporter(reporter);
        try {
            /*HashMap<Long, User> users = new HashMap<>(userMapper.selectList(null).size());

            for (UserModel userModel : userMapper.selectList((new QueryWrapper<UserModel>()).orderByAsc("id"))) {
                log.info("从数据库装配用户：{}", userModel.getUserId());
                User user = User.builder()
                        .userId(userModel.getUserId())
                        .client(new Client(userModel.getToken(), userModel.getLoginToken(), 229))
                        .build().initAddress();
                users.put(userModel.getUserId(), user);
            }
            manager.setUsers(users);*/
            log.info("装配{}个用户", userMapper.selectCount(null));
            log.info("从数据库装配任务");
            for (JobModel jobModel : jobMapper.selectList(null)) {
                manager.loadJob(jobModel);
            }
            log.info("装配任务完成,共{}个任务", jobMapper.selectCount(null));
            manager.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("Manager初始化完成");
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
