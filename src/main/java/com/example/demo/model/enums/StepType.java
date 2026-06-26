package com.example.demo.model.enums;

public enum StepType {
  ADDRESS("addressModificationStep", "userControlledAddressModificationJob"),
  NAME("nameModificationStep", "userControlledNameModificationJob"),
  OCCUPATION("occupationModificationStep", "userControlledOccupationModificationJob");

  private final String stepName;
  private final String jobName;

  StepType(String stepName, String jobName) {
    this.stepName = stepName;
    this.jobName = jobName;
  }

  public String getStepName() {
    return stepName;
  }

  public String getJobName() {
    return jobName;
  }
}
