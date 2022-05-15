package xyz.fumarase.killer.anlaiye;

import lombok.Setter;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.fumarase.killer.model.JobModel;


/**
 * @author YuanTao
 */
@Component
@Setter
public final class Runner implements org.quartz.Job {
    @Autowired
    private Manager manager;
    @Override
    public void execute(JobExecutionContext context){
        System.out.println("Manager");
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        manager.runJob((JobModel) jobDataMap.get("job"));
    }
}
