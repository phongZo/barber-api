package com.barber.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "db_barber_service")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Service extends Auditable<String>{
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "com.barber.api.service.id.IdGenerator")
  @GeneratedValue(generator = "idGenerator")
  private Long id;

  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "additional_info", columnDefinition = "TEXT")
  private String additionalInfo;

  private Integer duration;

  private Double price;

  private Double saleOff;

  private String image;

  private String video;

  private String tag;

  private Boolean allowDetail;

  @ManyToOne
  @JoinColumn(name = "category_id")
  private Category category;

  @Column(name = "options", columnDefinition = "TEXT")
  private String options;

  @Column(name = "service_step", columnDefinition = "TEXT")
  private String serviceStep;

  @OneToOne
  @JoinColumn(name = "parent_id")
  private Service parent;
}
