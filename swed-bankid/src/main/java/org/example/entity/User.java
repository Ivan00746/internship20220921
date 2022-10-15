package org.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String personalNumber;
    private String name;
    private String givenName;
    private String surname;

    public String getPersonalNumber() {
        return personalNumber;
    }

    public String getName() {
        return name;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getSurname() {
        return surname;
    }

    @Override
    public String toString() {
        return "User{" +
                "personalNumber='" + personalNumber + '\'' +
                ", name='" + name + '\'' +
                ", givenName='" + givenName + '\'' +
                ", surname='" + surname + '\'' +
                '}';
    }
}
