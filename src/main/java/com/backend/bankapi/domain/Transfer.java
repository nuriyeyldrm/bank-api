package com.backend.bankapi.domain;

import com.backend.bankapi.domain.enumeration.CurrencyCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transfers")
public class Transfer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Please write correct receiver account id")
    private Long fromAccountId;

    @NotNull(message = "Please write correct sender account id")
    private Long toAccountId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User userId;

    @NotNull(message = "Please specify transaction amount")
    @DecimalMin("10")
    @Column(nullable = false)
    private Double transactionAmount;

    private Double newBalance;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Please enter currency code")
    @Column(nullable = false)
    private CurrencyCode currencyCode;

    private Timestamp transactionDate;

    @NotNull(message = "Please write description")
    @Column(nullable = false)
    private String description;
}
