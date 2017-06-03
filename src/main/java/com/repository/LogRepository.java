package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entity.Log;

public interface LogRepository extends JpaRepository<Log, Long> {
}