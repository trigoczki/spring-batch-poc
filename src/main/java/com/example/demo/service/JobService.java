package com.example.demo.service;

import com.example.demo.constant.JobNames;
import com.example.demo.constant.JobParams;
import com.example.demo.exception.JobNotFoundException;
import com.example.demo.exception.LastStepNotCompletedException;
import com.example.demo.exception.LastStepNotFoundException;
import com.example.demo.model.dto.JobDto;
import com.example.demo.model.dto.SteppedJobDto;
import com.example.demo.model.entity.UserControlledJob;
import com.example.demo.model.entity.UserControlledJobStep;
import com.example.demo.model.enums.JobStatus;
import com.example.demo.model.enums.StepStatus;
import com.example.demo.model.enums.StepType;
import com.example.demo.repository.UserControlledJobRepository;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.JobExecutionException;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class JobService {

  private final List<StepType> stepOrder;

  private final UserControlledJobRepository userControlledJobRepository;
  private final JobRepository jobRepository;
  @Qualifier("asyncJobOperator")
  private final JobOperator jobOperator;
  private final JobRegistry jobRegistry;
  Logger logger = LoggerFactory.getLogger(JobService.class);

  public JobService(UserControlledJobRepository userControlledJobRepository,
      JobOperator jobOperator,
      JobRegistry jobRegistry, JobRepository jobRepository, List<StepType> stepOrder) {
    this.userControlledJobRepository = userControlledJobRepository;
    this.jobOperator = jobOperator;
    this.jobRegistry = jobRegistry;
    this.jobRepository = jobRepository;
    this.stepOrder = stepOrder;
  }

  public JobDto executeOneShotPersonModifierJob(Long personId) {
    try {
      Job job = jobRegistry.getJob(JobNames.PERSON_MODIFIER_JOB);
      JobParameters jobParameters = new JobParametersBuilder()
          .addLong(JobParams.PERSON_ID, personId)
          .addDate(JobParams.RUN_AT, new Date())
          .toJobParameters();

      JobExecution jobExecution = jobOperator.start(job, jobParameters);
      long jobId = jobExecution.getId();
      return new JobDto(jobId, JobNames.PERSON_MODIFIER_JOB);
    } catch (Exception e) {
      logger.error("Error executing personModifierJob: {}", e.getMessage(), e);
    }

    return null;
  }

  public JobDto startUserControlledJob(Long personId) {
    try {
      Job job = jobRegistry.getJob(StepType.NAME.getJobName());
      JobParameters jobParameters = new JobParametersBuilder()
          .addLong(JobParams.PERSON_ID, personId)
          .addDate(JobParams.RUN_AT, new Date())
          .toJobParameters();

      JobExecution jobExecution = jobOperator.start(job, jobParameters);
      long jobId = jobExecution.getId();
      UserControlledJob userControlledJob = new UserControlledJob();
      userControlledJob.setLastStepBatchJobExecutionId(jobId);
      userControlledJob.setStatus(JobStatus.IN_PROGRESS);
      UserControlledJob savedJob = userControlledJobRepository.save(userControlledJob);

      UserControlledJobStep jobStep = new UserControlledJobStep();
      jobStep.setExecutionId(jobId);
      jobStep.setJob(savedJob);
      jobStep.setType(StepType.NAME);
      jobStep.setStatus(StepStatus.IN_PROGRESS);
      userControlledJob.getSteps().add(jobStep);
      userControlledJobRepository.save(userControlledJob);

      return new JobDto(savedJob.getId(), StepType.NAME.getJobName());
    } catch (Exception e) {
      logger.error("Error starting {}: {}", StepType.NAME.getJobName(), e.getMessage(), e);
    }

    return null;
  }

  public SteppedJobDto continueUserControlledJob(Long jobId) {
    Optional<UserControlledJob> optionalContinuableJob = userControlledJobRepository.findById(
        jobId);
    if (optionalContinuableJob.isPresent()) {
      UserControlledJob userControlledJob = optionalContinuableJob.get();
      if (JobStatus.COMPLETED.equals(userControlledJob.getStatus())) {
        return new SteppedJobDto(jobId, JobStatus.COMPLETED,
            true, true, true);
      }
      JobExecution jobExecution = jobRepository.getJobExecution(
          userControlledJob.getLastStepBatchJobExecutionId());

      if (Objects.isNull(jobExecution)) {
        logger.error("Job not found! ID: {}", jobId);
        throw new JobNotFoundException();
      }

      StepStatus stepStatus = convertStatus(jobExecution.getStatus());
      if (!StepStatus.COMPLETED.equals(stepStatus)) {
        throw new LastStepNotCompletedException();
      }

      Optional<UserControlledJobStep> optionalLastStep = userControlledJob.getSteps().stream()
          .filter(
              s -> userControlledJob.getLastStepBatchJobExecutionId().equals(s.getExecutionId()))
          .findFirst();
      if (optionalLastStep.isEmpty()) {
        logger.error("Last job type not found! JobId: {}", jobId);
        throw new LastStepNotFoundException();
      }

      Long personId = jobExecution.getJobParameters().getLong(JobParams.PERSON_ID);
      StepType nextStepType = stepOrder.get(Math.min(stepOrder.size() - 1,
          stepOrder.indexOf(optionalLastStep.get().getType()) + 1));
      if (userControlledJob.getSteps().stream()
          .anyMatch(s -> nextStepType.equals(s.getType()) && StepStatus.COMPLETED.equals(
              s.getStatus()))) {
        return new SteppedJobDto(jobId, JobStatus.COMPLETED,
            true, true, true);
      }

      Job job = jobRegistry.getJob(nextStepType.getJobName());
      JobParameters jobParameters = new JobParametersBuilder()
          .addLong(JobParams.PERSON_ID, personId)
          .addDate(JobParams.RUN_AT, new Date())
          .toJobParameters();

      JobExecution newJobExecution = null;
      try {
        newJobExecution = jobOperator.start(job, jobParameters);
      } catch (JobExecutionException e) {
        logger.error("Error executing {}: {}", nextStepType.getJobName(), e.getMessage(), e);
        throw new RuntimeException(e);
      }
      long newJobId = newJobExecution.getId();

      UserControlledJobStep jobStep = new UserControlledJobStep();
      jobStep.setExecutionId(newJobId);
      jobStep.setJob(userControlledJob);
      jobStep.setType(nextStepType);
      jobStep.setStatus(StepStatus.IN_PROGRESS);
      userControlledJob.setLastStepBatchJobExecutionId(newJobId);
      userControlledJob.getSteps().add(jobStep);
      userControlledJobRepository.save(userControlledJob);
      List<UserControlledJobStep> steps = userControlledJob.getSteps();

      return new SteppedJobDto(jobId, userControlledJob.getStatus(),
          isStepFinished(steps, StepType.ADDRESS),
          isStepFinished(steps, StepType.NAME), isStepFinished(steps, StepType.OCCUPATION));
    } else {
      throw new JobNotFoundException();
    }
  }

  public SteppedJobDto getUserControlledJobStatus(Long jobId) {
    Optional<UserControlledJob> optionalContinuableJob = userControlledJobRepository.findById(
        jobId);
    if (optionalContinuableJob.isPresent()) {
      UserControlledJob userControlledJob = optionalContinuableJob.get();

      List<UserControlledJobStep> steps = userControlledJob.getSteps();
      boolean isAddressFinished = isStepFinished(steps, StepType.ADDRESS);
      boolean isNameFinished = isStepFinished(steps, StepType.NAME);
      boolean isOccupationFinished = isStepFinished(steps, StepType.OCCUPATION);

      if (isOccupationFinished) {
        userControlledJob.setStatus(JobStatus.COMPLETED);
      }
      userControlledJobRepository.save(userControlledJob);
      return new SteppedJobDto(jobId, userControlledJob.getStatus(), isAddressFinished,
          isNameFinished,
          isOccupationFinished);
    } else {
      throw new JobNotFoundException();
    }
  }

  private boolean isStepFinished(List<UserControlledJobStep> jobSteps, StepType type) {
    return jobSteps.stream().anyMatch(
        step -> type.equals(step.getType()) && StepStatus.COMPLETED.equals(step.getStatus()));
  }

  private StepStatus convertStatus(BatchStatus batchStatus) {
    return switch (batchStatus) {
      case COMPLETED -> StepStatus.COMPLETED;
      case STARTING, STARTED, STOPPING, STOPPED -> StepStatus.IN_PROGRESS;
      default -> StepStatus.FAILED;
    };
  }
}
