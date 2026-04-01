package com.barber.api.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "db_barber_customer")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
  @Id
  private Long id;

  private Date birthday;

  private Long score;

  private String otp;

  @Column(name="otp_fail_count")
  private Integer otpFailCount;

  @Column(name="otp_created_at")
  private Date otpCreatedAt;

  @OneToOne
  @MapsId
  @JoinColumn(name = "id")
  private Account account;
}
