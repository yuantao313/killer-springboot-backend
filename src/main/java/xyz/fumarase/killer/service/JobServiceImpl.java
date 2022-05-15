package xyz.fumarase.killer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.fumarase.killer.anlaiye.Manager;
import xyz.fumarase.killer.model.JobModel;
import xyz.fumarase.killer.mapper.JobMapper;

import java.util.List;

@Service("JobService")
public class JobServiceImpl implements IJobService {
    @Autowired
    private JobMapper jobMapper;
    @Autowired
    private Manager manager;

    @Override
    public void addJob(JobModel jobModel) {
        try {
            jobMapper.insert(jobModel);
            manager.addJob(jobModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateJob(String hash,JobModel jobModel) {
        try{
            JobModel oldJobModel = jobMapper.selectById(hash);
            jobMapper.updateById(jobModel);
            if(!oldJobModel.getEnable().equals(jobModel.getEnable())){
                if(jobModel.getEnable()){
                    manager.resumeJob(jobModel.getHash());
                }else{
                    manager.pauseJob(jobModel.getHash());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void deleteJob(String jobHash) {
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
        manager.runJob(jobMapper.selectById(hash));
    }
}
