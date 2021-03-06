package com.backend.bankapi.domain;

import com.backend.bankapi.domain.enumeration.AccountStatusType;
import com.backend.bankapi.domain.enumeration.AccountType;
import com.backend.bankapi.domain.enumeration.CurrencyCode;
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

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "account_no", referencedColumnName = "id", unique = true, nullable = false)
    private AccountNumber accountNo;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User userId;

    @Size(max = 250, message = "Size exceeded")
    @NotNull(message = "Please write description")
    @Column(nullable = false, length = 250)
    private String description;

    @Column(nullable = false)
    private Double balance = 0.0;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Please enter currency code")
    @Column(nullable = false)
    private CurrencyCode currencyCode;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Please choose account type")
    @Column(nullable = false)
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatusType accountStatusType;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "acc_modify_inf_id", referencedColumnName = "id")
    private AccountModifyInformation accModInfId;
}
