package com.backend.bankapi.projection;

import com.backend.bankapi.domain.enumeration.CurrencyCode;

import java.sql.Timestamp;

public interface ProjectTransferAdmin {

    Long getId();

    ProjectAccount getFromAccountId();

    Long getToAccountId();

    ProjectUser getUserId();

    Double getTransactionAmount();

    Double getNewBalance();

    CurrencyCode getCurrencyCode();

    Timestamp getTransactionDate();

    String getDescription();
}
