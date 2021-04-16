package com.job.scheduler.repository;

import com.job.scheduler.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchedulerRepo extends JpaRepository<Schedule, Long> {

    List<Schedule> findByPause(boolean pause);

    Schedule findByJobName(String jobName);
}
