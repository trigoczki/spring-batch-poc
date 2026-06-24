package com.example.demo.config.batch;

import com.example.demo.service.tasklet.AddressModifierTasklet;
import com.example.demo.service.tasklet.NameModifierTasklet;
import com.example.demo.service.tasklet.OccupationModifierTasklet;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class StepConfig {

  @Autowired
  private JobRepository jobRepository;

  @Autowired
  private PlatformTransactionManager transactionManager;

  @Autowired
  private AddressModifierTasklet addressModifierTasklet;

  @Autowired
  private NameModifierTasklet nameModifierTasklet;

  @Autowired
  private OccupationModifierTasklet occupationModifierTasklet;

  @Bean
  public Step nameModificationStep() {
    return new StepBuilder("nameModificationStep", jobRepository)
        .tasklet(nameModifierTasklet, transactionManager)
        .build();
  }

  @Bean
  public Step addressModificationStep() {
    return new StepBuilder("addressModificationStep", jobRepository)
        .tasklet(addressModifierTasklet, transactionManager)
        .build();
  }

  @Bean
  public Step occupationModificationStep() {
    return new StepBuilder("occupationModificationStep", jobRepository)
        .tasklet(occupationModifierTasklet, transactionManager)
        .build();
  }

}
