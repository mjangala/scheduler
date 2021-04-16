package com.job.scheduler.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImmediateJobDescriptor {
    @ApiModelProperty(required = true, value = "Job Name", example = "Job Example-2")
    private String jobName;
    @ApiModelProperty(required = true, value = "Job Group", example = "EMAIL_JOB")
    private String jobGroup;
    @ApiModelProperty(required = true, value = "To run the job Immediately", example = "true")
    private boolean runNow;
}
