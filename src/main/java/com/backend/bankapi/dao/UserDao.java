package com.backend.bankapi.dao;

import com.backend.bankapi.domain.ModifyInformation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDao {

    @Pattern(regexp = "^(?!000|666)[0-8][0-9]{2}-(?!00)[0-9]{2}-(?!0000)[0-9]{4}$", message = "Please enter valid SSN")
    @Size(min = 11, max= 11, message = "SSN should be exact 9 characters")
    @NotNull(message = "Please enter your SSN")
    @Column(nullable = false, unique = true, length = 11)
    private String ssn;

    @NotNull(message = "Please enter your first name")
    @Size(max = 15)
    @Column(nullable = false, length = 15)
    private String firstName;

    @NotNull(message = "Please enter your last name")
    @Size(max = 15)
    @Column(nullable = false, length = 15)
    private String lastName;

    @Email(message = "Please enter valid email")
    @Size(min = 5, max = 150)
    @NotNull(message = "Please enter your email")
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @JsonIgnore
    private String password;

    @Size(max = 250)
    @NotNull(message = "Please enter your address")
    @Column(nullable = false, length = 250)
    private String address;

    @Pattern(regexp = "^((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$",
            message = "Please enter valid phone number")
    @Size(min = 14, max= 14, message = "Phone number should be exact 10 characters")
    @NotNull(message = "Please enter your phone number")
    @Column(nullable = false, length = 14)
    private String mobilePhoneNumber;

    private ModifyInformation modifyInformation;

    public UserDao(String ssn, String firstName, String lastName, String email, String address,
                   String mobilePhoneNumber, ModifyInformation modifyInformation) {
        this.ssn = ssn;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.mobilePhoneNumber = mobilePhoneNumber;
        this.modifyInformation = modifyInformation;
    }
}
