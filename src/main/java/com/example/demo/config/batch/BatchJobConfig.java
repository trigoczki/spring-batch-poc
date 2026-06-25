package com.example.demo.config.batch;

import com.example.demo.model.enums.StepType;
import java.util.LinkedList;
import java.util.Map;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.EnableJdbcJobRepository;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
@EnableJdbcJobRepository
public class BatchJobConfig {

  /**
   * One-shot
   */
  @Bean
  public Job personModifierJob(JobRepository jobRepository, Map<String, Step> stepRegistry) {
    return new JobBuilder("personModifierJob", jobRepository)
        .start(stepRegistry.get("nameModificationStep"))
        .next(stepRegistry.get("addressModificationStep"))
        .next(stepRegistry.get("occupationModificationStep"))
        .build();
  }

  @Bean
  public Job steppedNameModificationJob(JobRepository jobRepository,
      Map<String, Step> stepRegistry) {
    return new JobBuilder(StepType.NAME.getJobName(), jobRepository)
        .start(stepRegistry.get(StepType.NAME.getStepName()))
        .build();
  }

  @Bean
  public Job steppedAddressModificationJob(JobRepository jobRepository,
      Map<String, Step> stepRegistry) {
    return new JobBuilder(StepType.ADDRESS.getJobName(), jobRepository)
        .start(stepRegistry.get(StepType.ADDRESS.getStepName()))
        .build();
  }

  @Bean
  public Job steppedOccupationModificationJob(JobRepository jobRepository,
      Map<String, Step> stepRegistry) {
    return new JobBuilder(StepType.OCCUPATION.getJobName(), jobRepository)
        .start(stepRegistry.get(StepType.OCCUPATION.getStepName()))
        .build();
  }

  @Bean
  public LinkedList<StepType> stepOrder() {
    LinkedList<StepType> steps = new LinkedList<>();
    steps.add(StepType.NAME);
    steps.add(StepType.ADDRESS);
    steps.add(StepType.OCCUPATION);

    return steps;
  }
}
