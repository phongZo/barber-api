package com.barber.api.repository;

import com.barber.api.model.ServiceStepTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ServiceStepTemplateRepository extends JpaRepository<ServiceStepTemplate, Long>,
    JpaSpecificationExecutor<ServiceStepTemplate> {

  Boolean existsByName(String name);
}
