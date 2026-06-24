package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Schedule one-shot batch jobs. Can be confiured by cron, too.
 */
@Component
public class Scheduler {

  private final JobService jobService;
  private final PersonService personService;
  Logger logger = LoggerFactory.getLogger(Scheduler.class);

  public Scheduler(JobService jobService, PersonService personService) {
    this.jobService = jobService;
    this.personService = personService;
  }

  @Scheduled(fixedRate = 10000)
  public void runJob1() {
    if (personService.existsById(1L)) {
      jobService.executePersonModifierJob(1L);
    } else {
      logger.warn("Person ID:1 not found! Job skipped!");
    }
  }

  @Scheduled(fixedRate = 10000)
  public void runJob2() {
    if (personService.existsById(2L)) {
      jobService.executePersonModifierJob(2L);
    } else {
      logger.warn("Person ID:2 not found! Job skipped!");
    }
  }

  @Scheduled(fixedRate = 10000)
  public void runJob3() {
    if (personService.existsById(3L)) {
      jobService.executePersonModifierJob(3L);
    } else {
      logger.warn("Person ID:3 not found! Job skipped!");
    }
  }
}
