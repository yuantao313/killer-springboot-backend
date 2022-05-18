package xyz.fumarase.killer.anlaiye;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;
import xyz.fumarase.killer.anlaiye.object.User;
import xyz.fumarase.killer.anlaiye.object.UserBuilder;
import xyz.fumarase.killer.mapper.HistoryMapper;
import xyz.fumarase.killer.mapper.JobMapper;
import xyz.fumarase.killer.mapper.UserMapper;
import xyz.fumarase.killer.model.HistoryModel;
import xyz.fumarase.killer.model.JobModel;
import xyz.fumarase.killer.model.UserModel;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author YuanTao
 */
@Component
@NoArgsConstructor
@Slf4j
public class Manager extends QuartzJobBean {
    //todo 重新构建ManagerFactory

    private Scheduler scheduler;

    @Autowired
    public void setSchedulerFactoryBean(SchedulerFactoryBean schedulerFactoryBean) {
        this.schedulerFactoryBean = schedulerFactoryBean;
    }

    private SchedulerFactoryBean schedulerFactoryBean;
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
            this.scheduler = schedulerFactoryBean.getScheduler();
            for (JobModel jobModel : jobMapper.selectList(null)) {
                log.info("从数据库装配任务：{}", jobModel);
                loadJob(jobModel);
            }
            log.info("装配任务完成,共{}个任务", jobMapper.selectCount(null));
            scheduler.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addJob(JobModel jobModel) {
        log.info("添加任务：{}", jobModel);
        try {
            jobMapper.insert(jobModel);
            loadJob(jobModel);
            log.info("添加任务成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("添加任务失败{}", e.getMessage());
        }
    }

    public void loadJob(JobModel jobModel) {
        log.info("装配任务：{}", jobModel);
        try {
            JobDetail jobDetail = JobBuilder.newJob(Manager.class)
                    .withIdentity(String.valueOf(jobModel.getId()), "JOB")
                    .build();
            jobDetail.getJobDataMap().put("jobId", jobModel.getId());
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(String.valueOf(jobModel.getId()), "TRIGGER")
                    .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(jobModel.getHour(), jobModel.getMinute()))
                    .build();
            scheduler.scheduleJob(jobDetail, trigger);
            log.info("成功：{}", jobModel);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("失败：{}", e.getMessage());
        }
    }

    public void deleteJob(int jobId) {
        log.info("删除任务：{}", jobId);
        try {
            jobMapper.deleteById(jobId);
            scheduler.deleteJob(new JobKey(String.valueOf(jobId), "JOB"));
            log.info("删除任务成功：{}", jobId);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("删除任务失败：{}", e.getMessage());
        }
    }

    public void trigJob(int id) {
        log.info("手动触发任务：{}", id);
        try {
            runJob(id);
            log.info("触发任务成功：{}", id);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("触发任务失败：{}", e.getMessage());
        }
    }

    public void updateJob(Integer jobId, JobModel jobModel) {
        log.info("更新任务：{}", jobModel);
        try {
            deleteJob(jobId);
            addJob(jobModel);
            log.info("更新任务成功：{}", jobModel);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("更新任务失败：{}", e.getMessage());
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
        historyModel.setStatus("RUNNING");
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        if("trigJob".equals(elements[2].getMethodName())) {
            historyModel.setIsManual(true);
        }else if("executeInternal".equals(elements[2].getMethodName())) {
            historyModel.setIsManual(false);
        }
        historyMapper.insert(historyModel);
        UpdateWrapper<HistoryModel> uw = (new UpdateWrapper<>());
        uw.eq("id", historyModel.getId());
        boolean isSuccess = getUser(jobModel.getSource())
                .setShop(jobModel.getShopId())
                .avoid(jobModel.getBlackList())
                .need(jobModel.getNeedList())
                .setTarget(jobModel.getTarget())
                .waitForShop()
                .run(jobModel.getTimeout());
        if (isSuccess) {
            historyMapper.update(historyModel, uw.set("status", "SUCCESS"));
        } else {
            historyMapper.update(historyModel, uw.set("status", "FAILED"));
        }
    }

    @Override
    protected void executeInternal(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        int jobId = (int) jobDataMap.get("jobId");
        runJob(jobId);
    }

    public List<JobModel> getJobs() {
        List<JobModel> jobModels = jobMapper.selectList(null);
        try {
            for (JobModel jobModel : jobModels) {
                Trigger trigger = scheduler.getTrigger(new TriggerKey(String.valueOf(jobModel.getId()), "TRIGGER"));
                assert trigger != null;
                jobModel.setNextRunTime(trigger.getNextFireTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jobModels;
    }

    public JobModel getJob(int id) {
        return jobMapper.selectById(id);
    }
}
