package com.barber.api.model.criteria;

import com.barber.api.model.Nation;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

@Data
public class NationCriteria {
  private static final long serialVersionUID = 1L;
  private Long id;
  private String name;
  private Integer kind;

  public Specification<Nation> getSpecification() {
    return new Specification<Nation>() {
      private static final long serialVersionUID = 1L;

      @Override
      public Predicate toPredicate(Root<Nation> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (getId() != null) {
          predicates.add(cb.equal(root.get("id"), getId()));
        }

        if (getKind() != null) {
          predicates.add(cb.equal(root.get("kind"), getKind()));
        }

        if (!StringUtils.isEmpty(getName())) {
          predicates.add(cb.like(cb.lower(root.get("name")), "%" + getName().toLowerCase() + "%"));
        }
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
