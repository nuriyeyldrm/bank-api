package com.backend.bankapi.dao;

import com.backend.bankapi.domain.ModifyInformation;
import com.backend.bankapi.domain.User;
import com.backend.bankapi.domain.enumeration.AccountStatusType;
import com.backend.bankapi.domain.enumeration.AccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDao {

    @Size(max = 250, message = "Size exceeded")
    @NotNull(message = "Please write description")
    private String description;

    @NotNull(message = "Please enter balance")
    private Double balance;

    @NotNull(message = "Please choose account type")
    private String accountType;

    @NotNull(message = "Please choose account status type")
    private String accountStatusType;
}
