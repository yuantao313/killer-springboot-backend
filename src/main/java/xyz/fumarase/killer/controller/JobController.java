package xyz.fumarase.killer.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.fumarase.killer.model.JobModel;
import xyz.fumarase.killer.service.JobServiceImpl;

import java.util.List;
import java.util.Map;


/**
 * @author YuanTao
 */
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

    @GetMapping("/job/{id}")
    public JobModel getJob(@PathVariable("id") Integer id) {
        return jobService.getJob(id);
    }

    @PostMapping("/job")
    public Boolean addJob(@RequestBody JobModel jobModel) {
        return jobService.addJob(jobModel);
    }

    @PutMapping("/job/{id}")
    public boolean updateJob(@PathVariable("id") Integer id, @RequestBody JobModel jobModel) {
        return jobService.updateJob(jobModel);
    }

    @PatchMapping("/job/{id}")
    public boolean patchJob(@PathVariable("id") Integer id, @RequestBody Map<String,Object> values) {
        return jobService.updateJob(id, values);
    }

    @DeleteMapping("/job/{id}")
    public boolean deleteJob(@PathVariable("id") Integer id) {
        return jobService.deleteJob(id);
    }

    @PostMapping("/job/{id}/trig")
    public boolean trigJob(@PathVariable("id") Integer id) {
        return jobService.trigJob(id);
    }

    @GetMapping("/job/{id}/pause")
    public boolean pauseJob(@PathVariable("id") Integer id) {
        return jobService.pauseJob(id);
    }
    @GetMapping("/job/{id}/resume")
    public boolean resumeJob(@PathVariable("id") Integer id) {
        return jobService.resumeJob(id);
    }
}
