package com.backend.bankapi.projection;

import java.sql.Timestamp;

public interface ProjectModifyInformation {

    String getCreatedBy();

    Timestamp getCreatedDate();

    String getLastModifiedBy();

    Timestamp getLastModifiedDate();
}
