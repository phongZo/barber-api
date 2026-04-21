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
@Table(name = "db_barber_booking_service")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingService extends Auditable<String>{
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "com.barber.api.service.id.IdGenerator")
  @GeneratedValue(generator = "idGenerator")
  private Long id;

  @Column(name = "service_id")
  private Long serviceId;

  @Column(name = "service_info")
  private String serviceInfo;

  private Double price;

  @ManyToOne
  @JoinColumn(name = "booking_id")
  private Booking booking;
}
