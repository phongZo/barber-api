package com.barber.api.repository;

import com.barber.api.model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface BranchRepository extends JpaRepository<Branch, Long>, JpaSpecificationExecutor<Branch> {

  Boolean existsByName(String name);

  @Modifying
  @Transactional
  @Query("UPDATE Branch b SET b.province = null WHERE b.province.id = :id")
  void setNullProvinceByProvinceId(@Param("id") Long id);

  @Modifying
  @Transactional
  @Query("UPDATE Branch b SET b.district = null WHERE b.district.id = :id")
  void setNullDistrictByDistrictId(@Param("id") Long id);

  @Modifying
  @Transactional
  @Query("UPDATE Branch b SET b.ward = null WHERE b.ward.id = :id")
  void setNullWardByWardId(@Param("id") Long id);
}
