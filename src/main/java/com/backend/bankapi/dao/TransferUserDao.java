package com.backend.bankapi.dao;

import com.backend.bankapi.domain.Transfer;
import com.backend.bankapi.domain.enumeration.CurrencyCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TransferUserDao {

    private Long id;

    private Long fromAccountId;

    private Long toAccountId;

    private Double transactionAmount;

    private CurrencyCode currencyCode;

    private Timestamp transactionDate;

    private String description;

    public TransferUserDao(Transfer transfer) {
        this.id = transfer.getId();
        this.fromAccountId = transfer.getFromAccountId().getAccountNo().getId();
        this.toAccountId = transfer.getToAccountId();
        this.transactionAmount = transfer.getTransactionAmount();
        this.currencyCode = transfer.getCurrencyCode();
        this.transactionDate = transfer.getTransactionDate();
        this.description = transfer.getDescription();
    }
}
