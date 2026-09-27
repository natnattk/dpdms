package com.rushinga.dpdms.drought_service;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DroughtIncidentRepository extends JpaRepository<DroughtIncident, Long> {
}