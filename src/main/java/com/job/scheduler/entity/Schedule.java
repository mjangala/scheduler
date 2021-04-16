package com.job.scheduler.entity;

import com.job.scheduler.listener.ScheduleEntityListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "SCHEDULE")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EntityListeners(ScheduleEntityListener.class)
public class Schedule {

    @Id
    private String jobName;

    @Column(name = "jobGroup")
    private String jobGroup;

    @Column(name = "cron")
    private String cron;

    @Column(name = "FixedInterval")
    private Integer fixedInterval;

    @Column(name = "priority")
    private int priority;

    @Column(name = "now")
    private boolean execute_now;

    @Column(name = "pause")
    private boolean pause;
}
