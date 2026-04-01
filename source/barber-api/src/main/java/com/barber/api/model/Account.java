package com.barber.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "db_barber_account")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Account extends Auditable<String> {
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.barber.api.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;

    private String username;

    private String phone;

    private String email;

    @Column(name = "full_name")
    private String fullName;

    @JsonIgnore
    private String password;

    private int kind;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Column(name = "avatar_path")
    private String avatarPath;

    @Column(name = "is_super_admin")
    private Boolean isSuperAdmin = false;
}
