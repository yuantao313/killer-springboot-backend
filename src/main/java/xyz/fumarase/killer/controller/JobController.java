package xyz.fumarase.killer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.fumarase.killer.model.JobModel;
import xyz.fumarase.killer.service.JobServiceImpl;

import java.util.HashMap;
import java.util.Map;


/**
 * @author YuanTao
 */
@RestController
@CrossOrigin
public class JobController {

    private JobServiceImpl jobService;

    @Autowired
    public void setJobService(JobServiceImpl jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/job")
    public MyResponse getJobs() {
        Map<String, Object> map = new HashMap<>();
        map.put("jobs", jobService.getJob());
        return new MyResponse(true, map);
    }

    @GetMapping("/job/{hash}")
    public MyResponse getJob(@PathVariable("hash") String hash) {
        Map<String, Object> map = new HashMap<>();
        map.put("job", jobService.getJob(hash));
        return new MyResponse(true, map);
    }

    @PostMapping("/job")
    public MyResponse addJob(@RequestBody JobModel jobModel) {
        jobService.addJob(jobModel.initHash());
        return new MyResponse(true);
    }

    @PutMapping("/job/{hash}")
    public MyResponse updateJob(@PathVariable("hash") String hash, @RequestBody JobModel jobModel) {
        jobService.updateJob(hash, jobModel);
        return new MyResponse(true);
    }

    @DeleteMapping("/job/{hash}")
    public MyResponse deleteJob(@PathVariable("hash") String hash) {
        jobService.deleteJob(hash);
        return new MyResponse(true);
    }

    @PostMapping("/job/{hash}/trig")
    public MyResponse trigJob(@PathVariable("hash") String hash) {
        jobService.trigJob(hash);
        return new MyResponse(true);
    }
}
