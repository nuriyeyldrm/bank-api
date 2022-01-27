package com.backend.bankapi.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminDao {

    private String ssn;

    @NotNull(message = "Please enter your first name")
    @Size(max = 15)
    private String firstName;

    @NotNull(message = "Please enter your last name")
    @Size(max = 15)
    private String lastName;

    @Email(message = "Please enter valid email")
    @Size(min = 5, max = 150)
    @NotNull(message = "Please enter your email")
    private String email;

    @Size(min = 4, max = 60, message = "Please enter min 4 characters")
    private String password;

    @Size(max = 250)
    @NotNull(message = "Please enter your address")
    private String address;

    @Pattern(regexp = "^((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$",
            message = "Please enter valid phone number")
    @Size(min = 14, max= 14, message = "Phone number should be exact 10 characters")
    @NotNull(message = "Please enter your phone number")
    private String mobilePhoneNumber;

    private Set<String> roles;
}
