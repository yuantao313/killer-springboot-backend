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
    @Autowired
    private JobServiceImpl jobService;

    @GetMapping("/job")
    public MyResponse job() {
        Map<String, Object> map = new HashMap<>();
        map.put("jobs", jobService.getJob());
        return new MyResponse(true, map);
    }

    @PostMapping("/job")
    public MyResponse job(@RequestBody JobModel jobModel) {
        jobService.addJob(jobModel.initHash());
        return new MyResponse(true);
    }

    @PutMapping("/job/{hash}")
    public MyResponse updateJob(@PathVariable("hash") String hash, @RequestBody JobModel jobModel) {
        jobService.updateJob(hash,jobModel);
        return new MyResponse(true);
    }

    @DeleteMapping("/job/{hash}")
    public MyResponse job(@PathVariable("hash") String hash) {
        jobService.deleteJob(hash);
        return new MyResponse(true);
    }

    @PostMapping("/job/{hash}/trig")
    public MyResponse runJob(@PathVariable("hash") String hash) {
        jobService.trigJob(hash);
        return new MyResponse(true);
    }
}
