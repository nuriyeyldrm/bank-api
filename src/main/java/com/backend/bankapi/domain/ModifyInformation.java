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

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "modifyInf")
public class ModifyInformation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedBy
    @Column(length = 50, updatable = false)
    private String createdBy;

    @CreatedDate
    @Column(updatable = false)
    private Timestamp createdDate;

    @LastModifiedBy
    @Column(length = 50)
    private String lastModifiedBy;

    @LastModifiedDate
    private Timestamp lastModifiedDate;

    public ModifyInformation(String createdBy, Timestamp createdDate,
                             String lastModifiedBy, Timestamp lastModifiedDate) {
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedDate = lastModifiedDate;
    }

    public ModifyInformation(String lastModifiedBy, Timestamp lastModifiedDate) {
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedDate = lastModifiedDate;
    }
}
