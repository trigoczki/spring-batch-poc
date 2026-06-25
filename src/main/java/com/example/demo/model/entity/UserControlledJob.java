package com.example.demo.model.entity;

import com.example.demo.model.enums.JobStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "USER_CONTROLLED_JOB")
public class UserControlledJob {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long Id;

  @Column(name = "LAST_STEP_BATCH_JOB_EXECUTION_ID", nullable = false)
  private Long lastStepBatchJobExecutionId;

  @Column(name = "STATUS", nullable = false)
  @Enumerated(EnumType.STRING)
  private JobStatus status;

  @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
  private List<UserControlledJobStep> steps = new ArrayList<>();

  public Long getId() {
    return Id;
  }

  public void setId(Long id) {
    Id = id;
  }

  public Long getLastStepBatchJobExecutionId() {
    return lastStepBatchJobExecutionId;
  }

  public void setLastStepBatchJobExecutionId(Long nextStepExecutionId) {
    this.lastStepBatchJobExecutionId = nextStepExecutionId;
  }

  public JobStatus getStatus() {
    return status;
  }

  public void setStatus(JobStatus status) {
    this.status = status;
  }

  public List<UserControlledJobStep> getSteps() {
    return steps;
  }

  public void setSteps(List<UserControlledJobStep> steps) {
    this.steps = steps;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserControlledJob that = (UserControlledJob) o;
    return Objects.equals(Id, that.Id) && Objects.equals(lastStepBatchJobExecutionId,
        that.lastStepBatchJobExecutionId) && status == that.status && Objects.equals(steps,
        that.steps);
  }

  @Override
  public int hashCode() {
    return Objects.hash(Id, lastStepBatchJobExecutionId, status, steps);
  }

  @Override
  public String toString() {
    return "UserControlledJob{" +
        "Id=" + Id +
        ", lastStepExecutionId=" + lastStepBatchJobExecutionId +
        ", status=" + status +
        ", steps=" + steps +
        '}';
  }
}
