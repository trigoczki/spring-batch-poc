package com.example.demo.model.dto;

import com.example.demo.model.enums.JobStatus;

public record SteppedJobDto(Long jobId, JobStatus jobStatus, boolean address, boolean name,
                            boolean occupation) {

}
