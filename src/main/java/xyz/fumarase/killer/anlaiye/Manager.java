package xyz.fumarase.killer.anlaiye;

import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.Data;
import org.quartz.*;
import xyz.fumarase.killer.anlaiye.object.User;
import xyz.fumarase.killer.model.JobModel;

import java.util.HashMap;

/**
 * @author YuanTao
 */
@Data
public class Manager extends BaseManager {
    private Scheduler scheduler;

    public void addJob(JobModel job) {
        try {
            JobDetail jobDetail = JobBuilder.newJob(Runner.class)
                    .withIdentity(job.getHash(), "JOB")
                    .build();
            JobDataMap jobDataMap = jobDetail.getJobDataMap();
            jobDataMap.put("job", job);
            jobDataMap.put("user", users.get(job.getSource()));
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(job.getHash(), "JOB")
                    .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(job.getHour(), job.getMinute()))
                    .build();
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteJob(String jobHash) {
        try {
            scheduler.deleteJob(new JobKey(jobHash, "JOB"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void trigJob(String jobHash) {
        try {
            scheduler.triggerJob(new JobKey(jobHash, "JOB"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateJob(String hash, JobModel jobModel) {
        try {
            scheduler.deleteJob(new JobKey(hash, "JOB"));
            addJob(jobModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
