package com.example.demo.repository;

import com.example.demo.model.entity.UserControlledJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContinuableJobRepository extends JpaRepository<UserControlledJob, Long> {

}
