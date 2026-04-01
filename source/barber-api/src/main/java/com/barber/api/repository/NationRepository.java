package com.barber.api.repository;

import com.barber.api.model.Nation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface NationRepository extends JpaRepository<Nation, Long>, JpaSpecificationExecutor<Nation> {

  Boolean existsByNameAndParentId(String name, Long parentId);

  boolean existsByNameAndKind(String name, Integer kind);

  boolean existsByNameAndKindAndIdNot(String name, Integer kind, Long currentId);

  boolean existsByNameAndParentIdAndIdNot(String name, Long parentId, Long currentId);

  @Modifying
  @Transactional
  @Query("UPDATE Nation n SET n.parent = null WHERE n.parent.id = :parentId")
  void setNullChildrenByParentId(@Param("parentId") Long parentId);
}
