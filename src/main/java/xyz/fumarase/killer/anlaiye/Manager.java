package xyz.fumarase.killer.anlaiye;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import xyz.fumarase.killer.anlaiye.client.Client;
import xyz.fumarase.killer.anlaiye.object.User;
import xyz.fumarase.killer.mapper.JobMapper;
import xyz.fumarase.killer.mapper.UserMapper;
import xyz.fumarase.killer.model.JobModel;
import xyz.fumarase.killer.model.UserModel;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author YuanTao
 */
@NoArgsConstructor
@Setter
@Slf4j
public class Manager {
    //todo job各操作原子化
    private HashMap<Long, User> users;
    private Scheduler scheduler;

    public void start() {
        try {
            this.scheduler.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private UserMapper userMapper;

    private JobMapper jobMapper;


    public void addUser(UserModel userModel) {
        User user = User.builder()
                .userId(userModel.getUserId())
                .client(new Client(userModel.getToken(), userModel.getLoginToken(), 229))
                .build().initAddress();
        users.put(user.getUserId(), user);
        userMapper.insert(userModel);
    }

    public void updateUser(UserModel userModel) {
        users.remove(userModel.getUserId());
        User user = User.builder()
                .userId(userModel.getUserId())
                .client(new Client(userModel.getToken(), userModel.getLoginToken(), 229))
                .build().initAddress();
        users.put(user.getUserId(), user);
        userMapper.updateById(userModel);
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
            log.info("成功");
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
            scheduler.triggerJob(new JobKey(String.valueOf(id), "JOB"));
            //注意处理数据库手动字段
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


    public List<JobModel> getJobs() {
        return  jobMapper.selectList(null);
    }

    public JobModel getJob(int id) {
        return jobMapper.selectById(id);
    }


}
