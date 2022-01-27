package com.backend.bankapi.dao;

import com.backend.bankapi.domain.ModifyInformation;
import com.backend.bankapi.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchDao {

    private Long id;

    private String ssn;

    private String firstName;

    private String lastName;

    private String email;

    private String address;

    private String mobilePhoneNumber;

    private Set<String> roles;

    private ModifyInformation modInfId;

    private Boolean buildIn;

    public SearchDao(User user) {
        this.id = user.getId();
        this.ssn = user.getSsn();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.address = user.getAddress();
        this.mobilePhoneNumber = user.getMobilePhoneNumber();
        this.roles = user.getRoles();
        this.modInfId = user.getModInfId();
        this.buildIn = user.getBuildIn();
    }
}
