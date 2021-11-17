package com.backend.bankapi.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "acc_modify_inf")
public class AccountModifyInformation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedBy
    @Column(length = 250, updatable = false)
    private String createdBy;

    @CreatedDate
    @Column(updatable = false)
    private Timestamp createdDate;

    @LastModifiedBy
    @Column(length = 250)
    private String lastModifiedBy;

    @LastModifiedDate
    private Timestamp lastModifiedDate;

    private Timestamp closedDate;

    public AccountModifyInformation(String createdBy, Timestamp createdDate,
                             String lastModifiedBy, Timestamp lastModifiedDate) {
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedDate = lastModifiedDate;
    }

    public AccountModifyInformation(Long id, String lastModifiedBy, Timestamp lastModifiedDate, Timestamp closedDate) {
        this.id = id;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedDate = lastModifiedDate;
        this.closedDate = closedDate;
    }

    public String setModifiedBy(String firstName, String lastName, Set<Role> roles) {
        return firstName.toLowerCase() + "_" + lastName.toLowerCase() + "_" + roles;
    }

    public Timestamp setDate() {
        Date date= new Date();
        long time = date.getTime();
        return lastModifiedDate = new Timestamp(time);
    }
}
