package com.backend.bankapi.dao;

import com.backend.bankapi.domain.AccountModifyInformation;
import com.backend.bankapi.domain.enumeration.AccountStatusType;
import com.backend.bankapi.domain.enumeration.AccountType;
import com.backend.bankapi.domain.enumeration.CurrencyCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDao {

    private Long id;

    @Size(max = 250, message = "Size exceeded")
    @NotNull(message = "Please write description")
    private String description;

    @NotNull(message = "Please enter balance")
    private Double balance;

    @NotNull(message = "Please enter currency code")
    private CurrencyCode currencyCode;

    @NotNull(message = "Please choose account type")
    private AccountType accountType;

    @NotNull(message = "Please choose account status type")
    private AccountStatusType accountStatusType;

    private Timestamp createdDate;

    private Timestamp closedDate;

    public AccountDao(Long id, String description, Double balance, CurrencyCode currencyCode, AccountType accountType,
                      AccountStatusType accountStatusType, AccountModifyInformation accountModifyInformation) {
        this.id = id;
        this.description = description;
        this.balance = balance;
        this.currencyCode = currencyCode;
        this.accountType = accountType;
        this.accountStatusType = accountStatusType;
        this.createdDate = accountModifyInformation.getCreatedDate();
    }

    public AccountDao(Long id, String description, Double balance, AccountType accountType,
                      AccountStatusType accountStatusType, Timestamp createdDate) {
        this.id = id;
        this.description = description;
        this.balance = balance;
        this.accountType = accountType;
        this.accountStatusType = accountStatusType;
        this.createdDate = createdDate;
    }
}
