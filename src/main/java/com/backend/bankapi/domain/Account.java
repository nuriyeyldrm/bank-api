package com.backend.bankapi.domain;

import com.backend.bankapi.domain.enumeration.AccountStatusType;
import com.backend.bankapi.domain.enumeration.AccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accounts")
public class Account implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User userId;

    @Size(max = 250, message = "Size exceeded")
    @NotNull(message = "Please write description")
    @Column(nullable = false, length = 250)
    private String description;

    @NotNull(message = "Please enter balance")
    @Column(nullable = false)
    private Double balance;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Please choose account type")
    @Column(nullable = false)
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Please choose account status type")
    @Column(nullable = false)
    private AccountStatusType accountStatusType;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "acc_modify_inf_id", referencedColumnName = "id")
    private AccountModifyInformation accModInfId;
}
