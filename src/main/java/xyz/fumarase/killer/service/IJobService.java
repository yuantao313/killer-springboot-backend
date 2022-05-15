package xyz.fumarase.killer.service;

import xyz.fumarase.killer.model.JobModel;

import java.util.List;

/**
 * @author YuanTao
 */

public interface IJobService {
    public void addJob(JobModel jobModel);

    public void updateJob(String hash,JobModel jobModel);
    public void deleteJob(String jobHash);
    public List<JobModel> getJob();
    public JobModel getJob(String hash);
    public void trigJob(String hash);
}
