package com.job.scheduler.service;

import com.job.scheduler.entity.Schedule;
import com.job.scheduler.repository.SchedulerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchedulerService {

    @Autowired
    private SchedulerRepo schedulerRepo;

    public List<Schedule> fetchActiveJobs() {
        return schedulerRepo.findByPause(false);
    }

    public Schedule fetchJob(String jobName){
        return schedulerRepo.findByJobName(jobName);
    }

    public Schedule save(Schedule schedule){
        return schedulerRepo.save(schedule);
    }
}
