package com.rushinga.dpdms.alert_service;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertLogRepository extends JpaRepository<AlertLog, Long> {
}
