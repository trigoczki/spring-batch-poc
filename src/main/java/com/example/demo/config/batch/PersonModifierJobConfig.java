package com.example.demo.config.batch;

import java.util.Map;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.EnableJdbcJobRepository;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
@EnableJdbcJobRepository
public class PersonModifierJobConfig {

  @Autowired
  private Map<String, Step> stepRegistry;

  @Bean
  public JobRegistry jobRegistry() {
    return new MapJobRegistry();
  }

  @Bean
  public Job personModifierJob(JobRepository jobRepository) {
    return new JobBuilder("personModifierJob", jobRepository)
        .start(stepRegistry.get("nameModificationStep"))
        .next(stepRegistry.get("addressModificationStep"))
        .next(stepRegistry.get("occupationModificationStep"))
        .build();
  }
}
