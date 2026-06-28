package com.example.demo.repository;

import com.example.demo.model.entity.UserControlledJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserControlledJobRepository extends JpaRepository<UserControlledJob, Long> {

}
