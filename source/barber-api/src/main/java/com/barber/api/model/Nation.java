package com.barber.api.model;

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
@Table(name = "db_barber_nation")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Nation extends Auditable<String>{
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "com.barber.api.service.id.IdGenerator")
  @GeneratedValue(generator = "idGenerator")
  private Long id;

  private String name;

  private Integer kind;

  @ManyToOne
  @JoinColumn(name = "parent_id")
  private Nation parent;
}
