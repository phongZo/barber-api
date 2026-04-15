package com.barber.api.model;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "db_barber_service_step_template")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServiceStepTemplate extends Auditable<String>{
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "com.barber.api.service.id.IdGenerator")
  @GeneratedValue(generator = "idGenerator")
  private Long id;
  private String name;
  private String image;
}
