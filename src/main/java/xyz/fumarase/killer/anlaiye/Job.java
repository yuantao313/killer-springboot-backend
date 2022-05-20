package xyz.fumarase.killer.anlaiye;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import xyz.fumarase.killer.anlaiye.object.User;
import xyz.fumarase.killer.model.JobModel;

/**
 * @author YuanTao
 */
@Component
public class Job extends QuartzJobBean {
    private Manager manager;

    @Autowired
    public void setManager(Manager manager) {
        this.manager = manager;
    }

    @Override
    public void executeInternal(JobExecutionContext context) throws JobExecutionException {
        int jobId = context.getJobDetail().getJobDataMap().getInt("jobId");
        manager.runJob(jobId);
    }
}
