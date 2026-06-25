package com.example.demo.controller;

import com.example.demo.model.dto.JobDto;
import com.example.demo.model.dto.PersonModifierRequest;
import com.example.demo.model.dto.SteppedJobDto;
import com.example.demo.service.JobService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("job")
public class JobController {

  private final JobService jobService;

  public JobController(JobService jobService) {
    this.jobService = jobService;
  }

  @PostMapping("/person-modifier/run")
  public JobDto runPersonModifierJob(@RequestBody @Valid PersonModifierRequest request) {
    return jobService.executeOneShotPersonModifierJob(request.personId());
  }

  @GetMapping("/user-controlled/status/{jobId}")
  public SteppedJobDto getUserControlledJobStatus(@PathVariable Long jobId) {
    return jobService.getUserControlledJobStatus(jobId);
  }

  @PostMapping("/user-controlled/start")
  public JobDto runStartUserControlledJob(
      @RequestBody @Valid PersonModifierRequest request) {
    return jobService.startUserControlledJob(request.personId());
  }

  @PostMapping("/user-controlled/continue")
  public SteppedJobDto continueUserControlledJob(
      @RequestBody @Valid JobDto request) {
    return jobService.continueUserControlledJob(request.jobId());
  }
}
