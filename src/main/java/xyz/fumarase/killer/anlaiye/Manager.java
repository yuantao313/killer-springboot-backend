package xyz.fumarase.killer.anlaiye;

import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.Interceptor;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import xyz.fumarase.killer.anlaiye.object.User;
import xyz.fumarase.killer.anlaiye.object.UserBuilder;
import xyz.fumarase.killer.mapper.HistoryMapper;
import xyz.fumarase.killer.mapper.JobMapper;
import xyz.fumarase.killer.mapper.UserMapper;
import xyz.fumarase.killer.model.HistoryModel;
import xyz.fumarase.killer.model.JobModel;
import xyz.fumarase.killer.model.UserModel;
import xyz.fumarase.killer.service.HistoryServiceImpl;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author YuanTao
 */
@Component
@NoArgsConstructor
public class Manager implements org.quartz.Job {
    private Scheduler scheduler;
    private final static Logger logger = LoggerFactory.getLogger(Manager.class);

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
    public void addUser(UserModel userModel) {
        userMapper.insert(userModel);
    }

    public User getUser(Long userId) {
        return UserBuilder.newUser().fromModel(userMapper.selectById(userId)).build();
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        for (UserModel userModel : userMapper.selectList(null)) {
            users.add(UserBuilder.newUser().fromModel(userModel).build());
        }
        return users;
    }

    public void deleteUser(Long userId) {
        userMapper.deleteById(userId);
    }

    @PostConstruct
    public void afterConstruct() {
        try {
            this.scheduler = StdSchedulerFactory.getDefaultScheduler();
            for (JobModel jobModel : jobMapper.selectList(null)) {
                logger.info("从数据库装配任务：{}", jobModel);
                loadJob(jobModel);
            }
            logger.info("装配任务完成,共{}个任务", jobMapper.selectCount(null));
            scheduler.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addJob(JobModel jobModel) {
        logger.info("添加任务：{}", jobModel);
        try {
            jobMapper.insert(jobModel);
            loadJob(jobModel);
            logger.info("添加任务成功");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("添加任务失败{}", e.getMessage());
        }
    }

    public void loadJob(JobModel jobModel) {
        logger.info("装配任务：{}", jobModel);
        try {
            JobDetail jobDetail = JobBuilder.newJob(Manager.class)
                    .withIdentity(String.valueOf(jobModel.getId()), "JOB")
                    .build();
            jobDetail.getJobDataMap().put("jobId", jobModel.getId());
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(String.valueOf(jobModel.getHash()), "TRIGGER")
                    .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(jobModel.getHour(), jobModel.getMinute()))
                    .build();
            scheduler.scheduleJob(jobDetail, trigger);
            logger.info("成功：{}", jobModel);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("失败：{}", e.getMessage());
        }
    }

    public void deleteJob(int jobId) {
        logger.info("删除任务：{}", jobId);
        try {
            jobMapper.deleteById(jobId);
            scheduler.deleteJob(new JobKey(String.valueOf(jobId), "JOB"));
            logger.info("删除任务成功：{}", jobId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("删除任务失败：{}", e.getMessage());
        }
    }

    public void trigJob(int jobId) {
        logger.info("触发任务：{}", jobId);
        try {
            scheduler.triggerJob(new JobKey(String.valueOf(jobId), "JOB"));
            logger.info("触发任务成功：{}", jobId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("触发任务失败：{}", e.getMessage());
        }
    }

    public void updateJob(Integer jobId, JobModel jobModel) {
        logger.info("更新任务：{}", jobModel);
        try {
            deleteJob(jobId);
            addJob(jobModel);
            logger.info("更新任务成功：{}", jobModel);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("更新任务失败：{}", e.getMessage());
        }
    }

    public void updateJob(Integer id, Map<String, Object> data) {
        JobModel jobModel = jobMapper.selectById(id);
        Class<JobModel> clazz = JobModel.class;
        for (String key : data.keySet()) {
            try {
                Field field = clazz.getDeclaredField(key);
                field.setAccessible(true);
                field.set(jobModel, data.get(key));
                field.setAccessible(false);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        updateJob(id, jobModel);
    }

    public void runJob(int jobId) {
        JobModel jobModel = jobMapper.selectById(jobId);
        HistoryModel historyModel = new HistoryModel();
        historyModel.setJobId(jobModel.getId());
        historyModel.setDatetime(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(System.currentTimeMillis()));
        historyMapper.insert(historyModel);
        getUser(jobModel.getSource())
                .setShop(jobModel.getShopId())
                .avoid(jobModel.getBlackList())
                .need(jobModel.getNeedList())
                .setTarget(jobModel.getTarget())
                .waitForShop()
                .run(jobModel.getTimeout());
    }

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        int jobId = (int) jobDataMap.get("jobId");
        runJob(jobId);
    }

    public List<JobModel> getJobs() {
        return jobMapper.selectList(null);
    }

    public JobModel getJob(int id) {
        return jobMapper.selectById(id);
    }
}
