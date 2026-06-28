package com.example.demo.config.batch;

import com.example.demo.constant.JobNames;
import com.example.demo.model.enums.StepType;
import com.example.demo.service.listener.StatusUpdater;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.EnableJdbcJobRepository;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Spring Batch jobs that handle person modification workflows.
 * <p>
 * This configuration supports two distinct use cases:
 * <ol>
 *   <li><b>One-shot job</b> ({@code personModifierJob}): Executes all modification steps
 *   sequentially without user intervention, following the order defined in the job configuration.
 *   The job can be triggered programmatically though client call or scheduling.
 *   {@link com.example.demo.service.Scheduler}</li>
 *
 *   <li><b>User-controlled jobs</b>: Each individual modification step from the one-shot job is
 *   exposed as a separate, standalone job. This allows users to trigger specific modifications
 *   (e.g., name change only) independently. The predefined sequence for the one-shot job is
 *   established by the {@code stepOrder()} bean.</li>
 * </ol>
 *
 * <h3>Step Order (User-controlled job only)</h3>
 * <ol start="1">
 *   <li>{@link StepType#NAME Name modification}</li>
 *   <li>{@link StepType#ADDRESS Address modification}</li>
 *   <li>{@link StepType#OCCUPATION Occupation modification}</li>
 * </ol>
 *
 * @see PersonModificationJobConfig#stepOrder() For the complete step sequence definition
 */
@Configuration
@EnableBatchProcessing
@EnableJdbcJobRepository
public class PersonModificationJobConfig {

  /**
   * One-shot job, executes all steps without user intervention
   */
  @Bean
  public Job personModifierJob(JobRepository jobRepository, Map<String, Step> stepRegistry) {
    return new JobBuilder(JobNames.PERSON_MODIFIER_JOB, jobRepository)
        .start(stepRegistry.get(StepType.NAME.getStepName()))
        .next(stepRegistry.get(StepType.ADDRESS.getStepName()))
        .next(stepRegistry.get(StepType.OCCUPATION.getStepName()))
        .build();
  }

  @Bean
  public Job userControlledNameModificationJob(JobRepository jobRepository,
      Map<String, Step> stepRegistry, StatusUpdater statusUpdater) {
    return new JobBuilder(StepType.NAME.getJobName(), jobRepository)
        .start(stepRegistry.get(StepType.NAME.getStepName()))
        .listener(statusUpdater)
        .build();
  }

  @Bean
  public Job userControlledAddressModificationJob(JobRepository jobRepository,
      Map<String, Step> stepRegistry, StatusUpdater statusUpdater) {
    return new JobBuilder(StepType.ADDRESS.getJobName(), jobRepository)
        .start(stepRegistry.get(StepType.ADDRESS.getStepName()))
        .listener(statusUpdater)
        .build();
  }

  @Bean
  public Job userControlledOccupationModificationJob(JobRepository jobRepository,
      Map<String, Step> stepRegistry, StatusUpdater statusUpdater) {
    return new JobBuilder(StepType.OCCUPATION.getJobName(), jobRepository)
        .start(stepRegistry.get(StepType.OCCUPATION.getStepName()))
        .listener(statusUpdater)
        .build();
  }

  @Bean
  public List<StepType> stepOrder() {
    LinkedList<StepType> steps = new LinkedList<>();
    steps.add(StepType.NAME);
    steps.add(StepType.ADDRESS);
    steps.add(StepType.OCCUPATION);

    return steps;
  }
}
