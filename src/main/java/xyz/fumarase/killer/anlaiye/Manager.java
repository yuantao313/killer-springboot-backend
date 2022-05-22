package xyz.fumarase.killer.anlaiye;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import xyz.fumarase.killer.anlaiye.client.Client;
import xyz.fumarase.killer.anlaiye.job.Job;
import xyz.fumarase.killer.anlaiye.job.Reporter;
import xyz.fumarase.killer.anlaiye.object.Shop;
import xyz.fumarase.killer.anlaiye.object.User;
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
@Setter
@Slf4j
public class Manager {
    //todo job各操作原子化
    private HashMap<Long, User> users;
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



    public Integer addUser(UserModel userModel) {
        User user = User.builder()
                .userId(userModel.getUserId())
                .client(new Client(userModel.getToken(), userModel.getLoginToken(), 229))
                .build().initAddress();
        users.put(userModel.getUserId(), user);
        userMapper.insert(userModel);
        return userModel.getId();
    }

    public void updateUser(UserModel userModel) {
        users.remove(userModel.getUserId());
        User user = User.builder()
                .userId(userModel.getUserId())
                .client(new Client(userModel.getToken(), userModel.getLoginToken(), 229))
                .build().initAddress();
        users.put(userModel.getUserId(), user);
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
        /*
        QueryWrapper<HistoryModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("job_id", id);
        queryWrapper.orderByDesc("time");
        queryWrapper.last("limit 1");
        HistoryModel newHistory = historyMapper.selectList(queryWrapper).get(0);
        //会不会出现lock？
        newHistory.setIsManual(true);
        while(scheduler.getTriggerState(TriggerKey.triggerKey(String.valueOf(id), "TRIGGER")) == Trigger.TriggerState.) {
        historyMapper.updateById(newHistory);*/
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

    private HashMap<Integer, Shop> shops;

    public Shop getShop(Integer id) {
        if (!shops.containsKey(id)) {
            shops.put(id, client.getShop(id));
        }
        return shops.get(id);
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
