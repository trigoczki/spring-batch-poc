package com.example.demo.config.batch;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.launch.support.TaskExecutorJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class StepRegistryConfig {

  @Bean
  public Map<String, Step> stepRegistry(Map<String, Step> steps) {
    return steps;
  }

  @Bean
  public JobRegistry jobRegistry() {
    return new MapJobRegistry();
  }

  @Bean
  public ThreadPoolTaskExecutor batchTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(5);
    executor.setQueueCapacity(25);
    executor.setThreadNamePrefix("batch-async-");
    executor.setRejectedExecutionHandler(new CallerRunsPolicy());
    executor.initialize();
    return executor;
  }

  @Bean(name = "asyncJobOperator")
  @Primary
  public TaskExecutorJobOperator jobOperator(JobRepository jobRepository, JobRegistry jobRegistry,
      ThreadPoolTaskExecutor batchTaskExecutor) {
    TaskExecutorJobOperator operator = new TaskExecutorJobOperator();
    operator.setJobRepository(jobRepository);
    operator.setJobRegistry(jobRegistry);
    operator.setTaskExecutor(batchTaskExecutor);

    return operator;
  }
}
