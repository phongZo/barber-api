package com.barber.api.model.criteria;

import com.barber.api.model.Category;
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
public class CategoryCriteria {
  private static final long serialVersionUID = 1L;
  private Long id;
  private String name;
  private Integer kind;
  private Long parentId;

  public Specification<Category> getSpecification() {
    return new Specification<Category>() {
      private static final long serialVersionUID = 1L;

      @Override
      public Predicate toPredicate(Root<Category> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
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

        if (getParentId() != null){
          predicates.add(cb.equal(root.get("parent").get("id"), getParentId()));
          query.orderBy(cb.asc(root.get("orderInParent")));
        }
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
