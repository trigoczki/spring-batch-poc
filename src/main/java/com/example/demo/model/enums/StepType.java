package com.example.demo.model.enums;

public enum StepType {
  ADDRESS("addressModificationStep", "steppedAddressModificationJob"),
  NAME("nameModificationStep", "steppedNameModificationJob"),
  OCCUPATION("occupationModificationStep", "steppedOccupationModificationJob");

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
