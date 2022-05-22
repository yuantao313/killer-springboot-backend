package xyz.fumarase.killer.service;

import io.swagger.models.auth.In;
import xyz.fumarase.killer.model.JobModel;

import java.util.List;
import java.util.Map;

/**
 * @author YuanTao
 */

public interface IJobService {
    boolean addJob(JobModel jobModel);

    boolean updateJob(JobModel jobModel);
    boolean updateJob(Integer id, Map<String,Object> value);

    boolean deleteJob(Integer id);
    List<JobModel> getJobs();
    JobModel getJob(Integer id);
    boolean trigJob(Integer id);

    boolean pauseJob(Integer id);
    boolean resumeJob(Integer id);
}
