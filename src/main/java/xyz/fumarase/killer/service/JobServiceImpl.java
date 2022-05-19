package xyz.fumarase.killer.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.fumarase.killer.anlaiye.Manager;
import xyz.fumarase.killer.model.JobModel;
import xyz.fumarase.killer.mapper.JobMapper;

import java.lang.reflect.Field;
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
}
