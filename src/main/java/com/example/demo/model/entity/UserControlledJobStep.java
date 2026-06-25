package com.example.demo.model.entity;

import com.example.demo.model.enums.StepStatus;
import com.example.demo.model.enums.StepType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "USER_CONTROLLED_JOB_STEP")
public class UserControlledJobStep {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long Id;

  @Column(name = "TYPE", nullable = false)
  @Enumerated(EnumType.STRING)
  private StepType type;

  @Column(name = "STATUS", nullable = false)
  @Enumerated(EnumType.STRING)
  private StepStatus status;

  @Column(name = "EXECUTION_ID", nullable = false)
  private Long executionId;

  @ManyToOne
  @JoinColumn(name = "user_controlled_job_id")
  private UserControlledJob job;

  public Long getId() {
    return Id;
  }

  public void setId(Long id) {
    Id = id;
  }

  public StepType getType() {
    return type;
  }

  public void setType(StepType type) {
    this.type = type;
  }

  public StepStatus getStatus() {
    return status;
  }

  public void setStatus(StepStatus status) {
    this.status = status;
  }

  public Long getExecutionId() {
    return executionId;
  }

  public void setExecutionId(Long executionId) {
    this.executionId = executionId;
  }

  public UserControlledJob getJob() {
    return job;
  }

  public void setJob(UserControlledJob job) {
    this.job = job;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserControlledJobStep that = (UserControlledJobStep) o;
    return Objects.equals(Id, that.Id) && type == that.type && status == that.status
        && Objects.equals(executionId, that.executionId) && Objects.equals(job,
        that.job);
  }

  @Override
  public int hashCode() {
    return Objects.hash(Id, type, status, executionId, job);
  }

  @Override
  public String toString() {
    return "UserControlledJobStep{" +
        "Id=" + Id +
        ", type=" + type +
        ", status=" + status +
        ", executionId=" + executionId +
        ", job=" + job +
        '}';
  }
}
