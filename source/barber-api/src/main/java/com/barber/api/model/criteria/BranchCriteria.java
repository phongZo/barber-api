package com.barber.api.model.criteria;

import com.barber.api.model.Branch;
import com.barber.api.model.Nation;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

@Data
public class BranchCriteria {
  private static final long serialVersionUID = 1L;
  private Long id;
  private String name;
  private String addressLine;
  private String phone;
  private Long wardId;
  private Long districtId;
  private Long provinceId;

  public Specification<Branch> getSpecification() {
    return new Specification<Branch>() {
      private static final long serialVersionUID = 1L;

      @Override
      public Predicate toPredicate(Root<Branch> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (getId() != null) {
          predicates.add(cb.equal(root.get("id"), getId()));
        }

        if (!StringUtils.isEmpty(getName())) {
          predicates.add(cb.like(cb.lower(root.get("name")), "%" + getName().toLowerCase() + "%"));
        }

        if (!StringUtils.isEmpty(getPhone())) {
          predicates.add(cb.like(cb.lower(root.get("phone")), "%" + getPhone().toLowerCase() + "%"));
        }

        if (getWardId() != null) {
          Join<Branch, Nation> nationJoin = root.join("ward");
          predicates.add(cb.equal(nationJoin.get("id"), getWardId()));
        }

        if (getDistrictId() != null) {
          Join<Branch, Nation> nationJoin = root.join("district");
          predicates.add(cb.equal(nationJoin.get("id"), getDistrictId()));
        }

        if (getProvinceId() != null) {
          Join<Branch, Nation> nationJoin = root.join("province");
          predicates.add(cb.equal(nationJoin.get("id"), getProvinceId()));
        }

        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
