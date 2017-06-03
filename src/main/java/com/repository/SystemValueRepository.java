package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.model.SystemValue;
import java.util.List;

public interface SystemValueRepository extends JpaRepository<SystemValue, Long> {
  public List<SystemValue> findByKey(String key); 
}