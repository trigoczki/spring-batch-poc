package com.example.demo.controller;

import com.example.demo.exception.JobNotFoundException;
import com.example.demo.exception.LastStepNotCompletedException;
import com.example.demo.exception.LastStepNotFoundException;
import com.example.demo.model.dto.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DemoExceptionHandler {

  @ExceptionHandler(JobNotFoundException.class)
  public ResponseEntity<Void> handleJobNotFoundException(JobNotFoundException ex) {
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(LastStepNotFoundException.class)
  public ResponseEntity<Void> handleLastStepNotFoundException(LastStepNotFoundException ex) {
    return new ResponseEntity<>(HttpStatus.CONFLICT);
  }

  @ExceptionHandler(LastStepNotCompletedException.class)
  public ResponseEntity<ErrorMessage> handleLastStepNotCompletedException(
      LastStepNotCompletedException ex) {
    return new ResponseEntity<>(new ErrorMessage("Previous step not finished"),
        HttpStatus.CONFLICT);
  }
}
