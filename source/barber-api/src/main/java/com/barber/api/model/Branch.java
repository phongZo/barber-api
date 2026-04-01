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
@Table(name = "db_barber_branch")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Branch extends Auditable<String>{
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "com.barber.api.service.id.IdGenerator")
  @GeneratedValue(generator = "idGenerator")
  private Long id;
  private String name;

  @Column(name = "address_line")
  private String addressLine;

  @ManyToOne
  @JoinColumn(name = "ward_id")
  private Nation ward;

  @ManyToOne
  @JoinColumn(name = "district_id")
  private Nation district;

  @ManyToOne
  @JoinColumn(name = "province_id")
  private Nation province;

  private String phone;

  @Column(columnDefinition = "text")
  private String setting;
}
