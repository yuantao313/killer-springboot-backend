package xyz.fumarase.killer.anlaiye;

import lombok.NoArgsConstructor;
import lombok.Setter;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.fumarase.killer.model.JobModel;


/**
 * @author YuanTao
 */
@Setter
@Component
@NoArgsConstructor
public class Runner implements org.quartz.Job {
    private static Manager manager;
    @Autowired
    public void setManager(Manager manager) {
        Runner.manager = manager;
    }
    @Override
    public void execute(JobExecutionContext context){
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        manager.runJob((JobModel) jobDataMap.get("job"));
    }
}
