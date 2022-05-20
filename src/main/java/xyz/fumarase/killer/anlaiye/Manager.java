package xyz.fumarase.killer.anlaiye;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import xyz.fumarase.killer.anlaiye.client.exception.ClientException;
import xyz.fumarase.killer.anlaiye.client.exception.EmptyOrderException;
import xyz.fumarase.killer.anlaiye.client.exception.OrderTimeoutException;
import xyz.fumarase.killer.anlaiye.client.exception.TokenInvalidException;
import xyz.fumarase.killer.anlaiye.object.User;
import xyz.fumarase.killer.anlaiye.object.UserBuilder;
import xyz.fumarase.killer.mapper.HistoryMapper;
import xyz.fumarase.killer.mapper.JobMapper;
import xyz.fumarase.killer.mapper.UserMapper;
import xyz.fumarase.killer.model.HistoryModel;
import xyz.fumarase.killer.model.JobModel;
import xyz.fumarase.killer.model.UserModel;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author YuanTao
 */
@NoArgsConstructor
@Data
@Slf4j
public class Manager {
    //todo job各操作原子化
    private HashMap<Long, User> users;
    private Scheduler scheduler;

    private UserMapper userMapper;

    private JobMapper jobMapper;

    private HistoryMapper historyMapper;


    public void addUser(UserModel userModel) {
        userMapper.insert(userModel);
        users.put(userModel.getUserId(), UserBuilder.newUser().fromModel(userModel).build());
    }

    public void updateUser(UserModel userModel) {
        userMapper.updateById(userModel);
        users.remove(userModel.getUserId());
        users.put(userModel.getUserId(), UserBuilder.newUser().fromModel(userModel).build());
    }

    public User getUser(Long userId) {
        return users.get(userId);
    }

    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public void deleteUser(Long userId) {
        log.info("删除用户：{}", userId);
        users.remove(userId);
        userMapper.deleteById(userId);
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
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(String.valueOf(jobModel.getId()), "TRIGGER")
                    .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(jobModel.getHour(), jobModel.getMinute()))
                    .build();
            if (scheduler.checkExists(new JobKey(String.valueOf(jobModel.getId()), "JOB"))) {
                log.info("任务已存在，更新任务");
                scheduler.rescheduleJob(trigger.getKey(), trigger);
            } else {
                log.info("任务不存在，添加任务");
                JobDetail jobDetail = JobBuilder.newJob(Job.class)
                        .withIdentity(String.valueOf(jobModel.getId()), "JOB")
                        .build();
                jobDetail.getJobDataMap().put("jobId", jobModel.getId());
                scheduler.scheduleJob(jobDetail, trigger);
            }
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

    public void updateJob(JobModel jobModel) {
        log.info("更新任务：{}", jobModel);
        try {
            jobMapper.updateById(jobModel);
            loadJob(jobModel);
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
        updateJob(jobModel);
    }

    public void runJob(int jobId) {
        JobModel jobModel = jobMapper.selectById(jobId);
        HistoryModel historyModel = new HistoryModel();
        historyModel.setJobId(jobModel.getId());
        historyModel.setStatus("RUNNING");
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        if ("trigJob".equals(elements[2].getMethodName())) {
            historyModel.setIsManual(true);
        } else if ("executeInternal".equals(elements[2].getMethodName())) {
            historyModel.setIsManual(false);
        }
        historyMapper.insert(historyModel);
        UpdateWrapper<HistoryModel> uw = (new UpdateWrapper<>());
        uw.eq("id", historyModel.getId());
        try {
            long orderId = getUser(jobModel.getSource())
                    .setShop(jobModel.getShopId())
                    .avoid(jobModel.getBlackList())
                    .need(jobModel.getNeedList())
                    .setTarget(jobModel.getTarget())
                    .waitForShop()
                    .run(jobModel.getTimeout());
            if (orderId > 0) {
                historyMapper.update(historyModel, uw.set("order_id", orderId));
                historyMapper.update(historyModel, uw.set("status", "SUCCESS"));
            } else {
                historyMapper.update(historyModel, uw.set("status", "UNKNOWN"));
            }
        } catch (TokenInvalidException e) {
            historyMapper.update(historyModel, uw.set("status", "INVALID TOKEN"));
        } catch (EmptyOrderException e) {
            historyMapper.update(historyModel, uw.set("status", "ORDER EMPTY"));
        } catch (OrderTimeoutException e) {
            historyMapper.update(historyModel, uw.set("status", "TIMEOUT"));
        } catch (ClientException e) {
            historyMapper.update(historyModel, uw.set("status", "UNKNOWN"));
        }
        //这里，也要考虑，使用enum
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
