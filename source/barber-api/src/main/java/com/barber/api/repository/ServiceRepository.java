package com.barber.api.repository;

import com.barber.api.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ServiceRepository extends JpaRepository<Service, Long>, JpaSpecificationExecutor<Service> {
  @Modifying
  @Transactional
  @Query("UPDATE Service s SET s.parent = null WHERE s.parent.id = :parentId")
  void setNullByParentId(Long parentId);

  @Modifying
  @Transactional
  @Query("UPDATE Service s SET s.category = null WHERE s.category.id = :categoryId")
  void setNullByCategoryId(Long categoryId);
}
