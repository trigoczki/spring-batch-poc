package com.example.demo.service.tasklet;

import com.example.demo.constant.JobParams;
import com.example.demo.model.entity.Person;
import com.example.demo.service.PersonService;
import java.util.Optional;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class AddressModifierTasklet implements Tasklet {

  private final PersonService personService;

  public AddressModifierTasklet(PersonService personService) {
    this.personService = personService;
  }

  @Override
  public @Nullable RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws InterruptedException {
    Long personId = (Long) chunkContext.getStepContext().getJobParameters()
        .get(JobParams.PERSON_ID);
    Optional<Person> personOptional = personService.getById(personId);
    if (personOptional.isEmpty()) {
      return RepeatStatus.FINISHED;
    }

    Person person = personOptional.get();
    person.setAddress(person.getAddress() + "_modified");

    //    Processing simulation
    Thread.sleep(2000);

    personService.save(person);

    return RepeatStatus.FINISHED;
  }
}
