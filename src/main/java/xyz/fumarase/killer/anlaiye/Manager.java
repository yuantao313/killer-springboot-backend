package xyz.fumarase.killer.anlaiye;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import xyz.fumarase.killer.anlaiye.client.Client;
import xyz.fumarase.killer.anlaiye.client.exception.TokenInvalidException;
import xyz.fumarase.killer.anlaiye.job.Job;
import xyz.fumarase.killer.anlaiye.object.Address;
import xyz.fumarase.killer.reporter.Reporter;
import xyz.fumarase.killer.anlaiye.object.Shop;
import xyz.fumarase.killer.mapper.HistoryMapper;
import xyz.fumarase.killer.mapper.JobMapper;
import xyz.fumarase.killer.mapper.UserMapper;
import xyz.fumarase.killer.model.HistoryModel;
import xyz.fumarase.killer.model.JobModel;
import xyz.fumarase.killer.model.UserModel;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author YuanTao
 */
@NoArgsConstructor
@Setter
@Slf4j
public class Manager {
    private Scheduler scheduler;
    private Client client;
    private Reporter reporter;

    public void start() {
        try {
            this.scheduler.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private UserMapper userMapper;

    private JobMapper jobMapper;

    private HistoryMapper historyMapper;


    @CacheEvict(value = "users", allEntries = true)
    public Integer addUser(UserModel userModel) {
        log.info("添加用户：{}", userModel);
        userMapper.insert(userModel);
        return userModel.getId();
    }

    @CacheEvict(value = "user", key = "#userModel.userId")
    public void updateUser(UserModel userModel) {
        log.info("更新用户：{}", userModel);
        userMapper.updateById(userModel);
    }

    @CachePut(value = "user", key = "#userId")
    public UserModel getUser(Long userId) {
        log.info("获取用户：{}", userId);
        return userMapper.selectById(userId).afterLoad();
    }

    @Cacheable(value = "users")
    public List<UserModel> getUsers() {
        log.info("获取用户列表");
        return userMapper.selectList(null).stream().map(UserModel::afterLoad).collect(Collectors.toList());
    }

    @CacheEvict(value = "user", allEntries = true)
    public void deleteUser(Long userId) {
        log.info("删除用户：{}", userId);
        userMapper.deleteById(userId);
    }

    @CacheEvict(value = "jobs", allEntries = true)
    public Integer addJob(JobModel jobModel) {
        log.info("添加任务：{}", jobModel);
        try {
            jobMapper.insert(jobModel);
            loadJob(jobModel);
            log.info("添加任务成功");
            return jobModel.getId();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("添加任务失败{}", e.getMessage());
        }
        return null;
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
            if (!jobModel.getEnable()) {
                pauseJob(jobModel.getId());
            }
            log.info("成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("失败：{}", e.getMessage());
        }
    }

    @CacheEvict(value = "jobs", allEntries = true)
    @SneakyThrows(SchedulerException.class)
    public void deleteJob(int jobId) {
        log.info("删除任务：{}", jobId);
        jobMapper.deleteById(jobId);
        scheduler.deleteJob(new JobKey(String.valueOf(jobId), "JOB"));
        log.info("删除任务成功：{}", jobId);
    }

    @SneakyThrows(SchedulerException.class)
    public void trigJob(int id) {
        log.info("手动触发任务：{}", id);
        scheduler.triggerJob(new JobKey(String.valueOf(id), "JOB"));
    }

    @CacheEvict(value = "job", key = "#jobModel.id")
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

    @CacheEvict(value = "job", key = "#jobModel.id")
    @SneakyThrows
    public void updateJob(Integer id, Map<String, Object> data) {
        JobModel jobModel = jobMapper.selectById(id);
        Class<JobModel> clazz = JobModel.class;
        for (String key : data.keySet()) {
            Field field = clazz.getDeclaredField(key);
            field.setAccessible(true);
            field.set(jobModel, data.get(key));
            field.setAccessible(false);
        }
        updateJob(jobModel);
    }

    @Cacheable(value = "shop", key = "#id")
    public Shop getShop(Integer id) {
        return client.getShop(id);

    }

    public List<JobModel> getJobs() {
        return jobMapper.selectList(null);
    }

    public JobModel getJob(int id) {
        return jobMapper.selectById(id);
    }

    @SneakyThrows
    public void pauseJob(int id) {
        log.info("暂停任务：{}", id);
        jobMapper.updateById(jobMapper.selectById(id).setEnable(false));
        scheduler.pauseJob(new JobKey(String.valueOf(id), "JOB"));
    }

    @SneakyThrows
    public void resumeJob(int id) {
        log.info("恢复任务：{}", id);
        jobMapper.updateById(jobMapper.selectById(id).setEnable(true));
        scheduler.resumeJob(new JobKey(String.valueOf(id), "JOB"));
    }


    public Integer addHistory(HistoryModel historyModel) {
        historyMapper.insert(historyModel);
        return historyModel.getId();
    }

    public void updateHistory(HistoryModel historyModel) {
        historyMapper.updateById(historyModel);
    }

    public void deleteHistory(int id) {
        historyMapper.deleteById(id);
    }

    public void setReporter(Reporter reporter) {
        this.reporter = reporter;
    }

}
