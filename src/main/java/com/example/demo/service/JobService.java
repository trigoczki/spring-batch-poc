package com.example.demo.service;

import com.example.demo.model.dto.JobDto;
import java.util.Date;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.stereotype.Service;

@Service
public class JobService {

  private final JobOperator jobOperator;
  private final JobRegistry jobRegistry;

  public JobService(JobOperator jobOperator, JobRegistry jobRegistry) {
    this.jobOperator = jobOperator;
    this.jobRegistry = jobRegistry;
  }

  public JobDto executePersonModifierJob(Long personId) {
    try {
      Job job = jobRegistry.getJob("personModifierJob");
      JobParameters jobParameters = new JobParametersBuilder()
          .addLong("person_id", personId)
          .addDate("run_at", new Date())
          .toJobParameters();

      // TODO find a way to start job async, we need to return the job ID
      JobExecution jobExecution = jobOperator.start(job, jobParameters);
      long jobId = jobExecution.getId();
      return new JobDto(jobId, "personModifierJob");
    } catch (Exception e) {
      System.err.println("Error executing personModifierJob: " + e.getMessage());
      e.printStackTrace();
    }

    return null;
  }
}
