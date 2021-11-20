package com.backend.bankapi.dao;

import com.backend.bankapi.domain.User;
import com.backend.bankapi.domain.enumeration.CurrencyCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TransferDao {

    private Long id;

    @NotNull(message = "Please write correct receiver account id")
    private Long fromAccountId;

    @NotNull(message = "Please write correct sender account id")
    private Long toAccountId;

    private User userId;

    @NotNull(message = "Please specify transaction amount")
    @DecimalMin("10")
    private Double transactionAmount;

    private Double newBalance;

    @NotNull(message = "Please enter currency code")
    private CurrencyCode currencyCode;

    private Timestamp transactionDate;

    @NotNull(message = "Please write description")
    private String description;
}
