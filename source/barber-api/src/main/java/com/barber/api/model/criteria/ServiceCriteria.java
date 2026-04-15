package com.barber.api.model.criteria;

import com.barber.api.model.Category;
import com.barber.api.model.Service;
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
public class ServiceCriteria {
  private static final long serialVersionUID = 1L;
  private Long id;
  private String name;
  private Long categoryId;
  private Long parentId;

  public Specification<Service> getSpecification() {
    return new Specification<Service>() {
      private static final long serialVersionUID = 1L;

      @Override
      public Predicate toPredicate(Root<Service> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (getId() != null) {
          predicates.add(cb.equal(root.get("id"), getId()));
        }

        if (!StringUtils.isEmpty(getName())) {
          predicates.add(cb.like(cb.lower(root.get("name")), "%" + getName().toLowerCase() + "%"));
        }

        if (getCategoryId() != null){
          Join<Service, Category> categoryJoin = root.join("category");
          predicates.add(cb.equal(categoryJoin.get("id"), getCategoryId()));
        }

        if (getParentId() != null){
          predicates.add(cb.equal(root.get("parent").get("id"), getParentId()));
        }
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
