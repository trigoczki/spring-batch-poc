package com.example.demo.controller;

import com.example.demo.model.dto.JobDto;
import com.example.demo.model.dto.PersonModifierRequest;
import com.example.demo.service.JobService;
import jakarta.validation.Valid;
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
    return jobService.executePersonModifierJob(request.personId());
  }
}
