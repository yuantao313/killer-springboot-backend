package xyz.fumarase.killer.service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.fumarase.killer.object.Manager;
import xyz.fumarase.killer.model.JobModel;

import java.util.List;
import java.util.Map;

/**
 * @author YuanTao
 */
@Service("JobService")
@NoArgsConstructor
@Slf4j
public class JobServiceImpl implements IJobService {

    private Manager manager;

    @Autowired
    public void setManager(Manager manager) {
        this.manager = manager;
    }


    @Override
    public boolean addJob(JobModel jobModel) {
        log.info("添加任务：" + jobModel.toString());
        try {
            manager.addJob(jobModel);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateJob(JobModel jobModel) {
        try {
            manager.updateJob(jobModel);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateJob(Integer id, Map<String, Object> value) {
        manager.updateJob(id, value);
        return true;
    }

    @Override
    public boolean deleteJob(Integer id) {
        try {
            manager.deleteJob(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<JobModel> getJobs() {
        return manager.getJobs();
    }

    @Override
    public JobModel getJob(Integer id) {
        return manager.getJob(id);
    }

    @Override
    public boolean trigJob(Integer id) {
        try {
            manager.trigJob(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean pauseJob(Integer id) {
        try {
            manager.pauseJob(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean resumeJob(Integer id) {
        try {
            manager.resumeJob(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
