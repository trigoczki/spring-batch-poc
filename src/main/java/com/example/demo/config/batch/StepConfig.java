package com.example.demo.config.batch;

import com.example.demo.model.enums.StepType;
import com.example.demo.service.tasklet.AddressModifierTasklet;
import com.example.demo.service.tasklet.NameModifierTasklet;
import com.example.demo.service.tasklet.OccupationModifierTasklet;
import java.util.Map;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class StepConfig {

  @Bean
  public Map<String, Step> stepRegistry(Map<String, Step> steps) {
    return steps;
  }

  @Bean
  public Step nameModificationStep(JobRepository jobRepository,
      PlatformTransactionManager transactionManager, NameModifierTasklet tasklet) {
    return new StepBuilder(StepType.NAME.getStepName(), jobRepository)
        .tasklet(tasklet, transactionManager)
        .build();
  }

  @Bean
  public Step addressModificationStep(JobRepository jobRepository,
      PlatformTransactionManager transactionManager, AddressModifierTasklet tasklet) {
    return new StepBuilder(StepType.ADDRESS.getStepName(), jobRepository)
        .tasklet(tasklet, transactionManager)
        .build();
  }

  @Bean
  public Step occupationModificationStep(JobRepository jobRepository,
      PlatformTransactionManager transactionManager, OccupationModifierTasklet tasklet) {
    return new StepBuilder(StepType.OCCUPATION.getStepName(), jobRepository)
        .tasklet(tasklet, transactionManager)
        .build();
  }

}
