package com.backend.bankapi.projection;

import java.util.Set;

public interface ProjectUser {

    String getSsn();

    String getFirstName();

    String getLastName();

    String getEmail();

    String getAddress();

    String getMobilePhoneNumber();

    Set<String> getRoles();
}
