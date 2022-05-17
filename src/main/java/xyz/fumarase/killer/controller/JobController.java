package xyz.fumarase.killer.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.fumarase.killer.response.BaseResponse;
import xyz.fumarase.killer.model.JobModel;
import xyz.fumarase.killer.service.JobServiceImpl;

import java.util.List;
import java.util.Map;


/**
 * @author YuanTao
 */
@BaseResponse
@Api(tags={ "任务" })
@RestController
@CrossOrigin
public class JobController {

    private JobServiceImpl jobService;

    @Autowired
    public void setJobService(JobServiceImpl jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/job")
    public List<JobModel> getJobs() {
        return jobService.getJobs();
    }

    @GetMapping("/job/{hash}")
    public JobModel getJob(@PathVariable("hash") Integer id) {
        return jobService.getJob(id);
    }

    @PostMapping("/job")
    public Boolean addJob(@RequestBody JobModel jobModel) {
        return jobService.addJob(jobModel);
    }

    @PutMapping("/job/{hash}")
    public boolean updateJob(@PathVariable("hash") Integer id, @RequestBody JobModel jobModel) {
        return jobService.updateJob(id, jobModel);
    }

    @PatchMapping("/job/{hash}")
    public boolean patchJob(@PathVariable("hash") Integer id, @RequestBody Map<String,Object> values) {
        return jobService.updateJob(id, values);
    }

    @DeleteMapping("/job/{hash}")
    public boolean deleteJob(@PathVariable("hash") Integer id) {
        return jobService.deleteJob(id);
    }

    @PostMapping("/job/{hash}/trig")
    public boolean trigJob(@PathVariable("hash") Integer id) {
        return jobService.trigJob(id);
    }
}
