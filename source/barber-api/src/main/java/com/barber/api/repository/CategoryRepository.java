package com.barber.api.repository;

import com.barber.api.model.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CategoryRepository extends JpaRepository<Category, Long>,
    JpaSpecificationExecutor<Category> {

  Boolean existsByNameAndKind(String name, Integer kind);

  Boolean existsByNameAndKindAndParentId(String name, Integer kind, Long parentId);

  @Query("SELECT COALESCE(max(c.orderInParent), 0) from Category c WHERE " +
      "c.kind = :kind and " +
      "((:parentId IS NULL and c.parent IS NULL) OR c.parent.id = :parentId)")
  Integer findMaxOrderInParent(@Param("kind") Integer kind, @Param("parentId") Long parentId);

  @Modifying
  @Transactional
  @Query("UPDATE Category c SET c.orderInParent = c.orderInParent - 1 " +
      "WHERE c.kind = :kind AND " +
      "(:parentId IS NULL AND c.parent IS NULL OR c.parent.id = :parentId) " +
      "AND c.orderInParent > :removedOrder")
  void decreaseOrderAfterRemove(@Param("kind") Integer kind, @Param("parentId") Long parentId, @Param("removedOrder") Integer removedOrder);

  @Modifying
  @Transactional
  @Query("UPDATE Category c SET c.parent = null WHERE c.parent.id = :parentId")
  void setNullChildrenByParentId(@Param("parentId") Long parentId);

  @Modifying
  @Transactional
  @Query("UPDATE Category c SET c.orderInParent = c.orderInParent + 1 " +
      "WHERE c.kind = :kind AND " +
      "((:parentId IS NULL AND c.parent IS NULL) OR c.parent.id = :parentId) " +
      "AND c.orderInParent BETWEEN :start AND :end")
  void increaseOrderRange(@Param("kind") Integer kind, @Param("parentId") Long parentId, @Param("start") Integer start, @Param("end") Integer end);

  @Modifying
  @Transactional
  @Query("UPDATE Category c SET c.orderInParent = c.orderInParent - 1 " +
      "WHERE c.kind = :kind AND " +
      "((:parentId IS NULL AND c.parent IS NULL) OR c.parent.id = :parentId) " +
      "AND c.orderInParent BETWEEN :start AND :end")
  void decreaseOrderRange(@Param("kind") Integer kind, @Param("parentId") Long parentId, @Param("start") Integer start, @Param("end") Integer end);
}
