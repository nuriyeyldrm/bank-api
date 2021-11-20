package com.backend.bankapi.dao;

import com.backend.bankapi.domain.Account;
import com.backend.bankapi.domain.enumeration.CurrencyCode;

import java.sql.Timestamp;

public interface ProjectTransfer {

    Long getId();

    ProjectAccount getFromAccountId();

    Long getToAccountId();

    Double getTransactionAmount();

    Double getNewBalance();

    CurrencyCode getCurrencyCode();

    Timestamp getTransactionDate();

    String getDescription();
}
