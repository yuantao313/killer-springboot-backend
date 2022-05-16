package xyz.fumarase.killer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.fumarase.killer.anlaiye.Manager;
import xyz.fumarase.killer.model.JobModel;
import xyz.fumarase.killer.mapper.JobMapper;

import java.util.List;

/**
 * @author YuanTao
 */
@Service("JobService")
public class JobServiceImpl implements IJobService {
    private static final Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);

    private JobMapper jobMapper;

    @Autowired
    public void setJobMapper(JobMapper jobMapper) {
        this.jobMapper = jobMapper;
    }

    private Manager manager;

    @Autowired
    public void setManager(Manager manager) {
        this.manager = manager;
    }


    @Override
    public void addJob(JobModel jobModel) {
        try {
            logger.info("添加任务：" + jobModel.toString());
            jobMapper.insert(jobModel);
            manager.addJob(jobModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateJob(String hash, JobModel jobModel) {
        try {
            jobMapper.updateById(jobModel);
            manager.updateJob(hash, jobModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteJob(String jobHash) {
        logger.info("删除任务：" + jobHash);
        jobMapper.deleteById(jobHash);
        manager.deleteJob(jobHash);
    }

    @Override
    public List<JobModel> getJob() {
        return jobMapper.selectList(null);
    }

    @Override
    public JobModel getJob(String hash) {
        return jobMapper.selectById(hash);
    }

    @Override
    public void trigJob(String hash) {
        logger.info("触发任务：" + hash);
        manager.trigJob(hash);
    }
}
