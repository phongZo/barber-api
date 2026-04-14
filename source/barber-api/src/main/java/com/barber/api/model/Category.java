package com.barber.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "db_barber_category")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category extends Auditable<String>{
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "com.barber.api.service.id.IdGenerator")
  @GeneratedValue(generator = "idGenerator")
  private Long id;

  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  private String image;

  private Integer kind;

  @Column(name = "additional_info", columnDefinition = "TEXT")
  private String additionalInfo;

  @Column(name = "order_in_parent")
  private Integer orderInParent;

  @Column(name = "is_selected")
  private Boolean isSelected;

  @ManyToOne
  @JoinColumn(name = "parent_id")
  private Category parent;
}
