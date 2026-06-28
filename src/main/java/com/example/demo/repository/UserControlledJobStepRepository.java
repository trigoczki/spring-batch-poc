package com.example.demo.repository;

import com.example.demo.model.entity.UserControlledJobStep;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserControlledJobStepRepository extends
    JpaRepository<UserControlledJobStep, Long> {

}
