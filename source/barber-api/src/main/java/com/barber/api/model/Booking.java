package com.barber.api.model;

import java.util.Date;
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
@Table(name = "db_barber_booking")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Booking extends Auditable<String>{
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "com.barber.api.service.id.IdGenerator")
  @GeneratedValue(generator = "idGenerator")
  private Long id;

  private Double discount;

  @Column(name = "total_price")
  private Double totalPrice;

  @Column(name = "booking_date")
  private Date bookingDate;

  @Column(columnDefinition = "TEXT", name = "customer_info")
  private String customerInfo;

  @ManyToOne
  @JoinColumn(name = "customer_id")
  private Customer customer;

  @ManyToOne
  @JoinColumn(name = "branch_id")
  private Branch branch;
}
