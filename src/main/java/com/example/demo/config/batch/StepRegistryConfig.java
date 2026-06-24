package com.example.demo.config.batch;

import java.util.Map;
import org.springframework.batch.core.step.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StepRegistryConfig {

  @Autowired
  private Map<String, Step> steps;

  @Bean
  public Map<String, Step> stepRegistry() {
    return steps;
  }
}
