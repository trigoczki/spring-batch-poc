package com.example.demo.service.listener;

import com.example.demo.model.entity.UserControlledJobStep;
import com.example.demo.model.enums.JobStatus;
import com.example.demo.model.enums.StepStatus;
import com.example.demo.model.enums.StepType;
import com.example.demo.repository.UserControlledJobStepRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.stereotype.Service;

@Service
public class StatusUpdater implements JobExecutionListener {

  private final List<StepType> stepOrder;
  private final UserControlledJobStepRepository userControlledJobStepRepository;

  Logger logger = LoggerFactory.getLogger(StatusUpdater.class);

  public StatusUpdater(List<StepType> stepOrder,
      UserControlledJobStepRepository userControlledJobStepRepository) {
    this.stepOrder = stepOrder;
    this.userControlledJobStepRepository = userControlledJobStepRepository;
  }


  @Override
  public void afterJob(JobExecution jobExecution) {
    long jobId = jobExecution.getId();
    Optional<UserControlledJobStep> optionalStep = userControlledJobStepRepository.findById(jobId);
    if (optionalStep.isPresent() && BatchStatus.COMPLETED.equals(jobExecution.getStatus())) {
      UserControlledJobStep step = optionalStep.get();
      step.setStatus(StepStatus.COMPLETED);

      StepType lastStep = stepOrder.reversed().get(0);
      if (lastStep.equals(step.getType())) {
        step.getJob().setStatus(JobStatus.COMPLETED);
      }
      userControlledJobStepRepository.save(step);

      logger.info("After job: {}", jobExecution.getStatus());
    }
  }
}
