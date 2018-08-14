package com.zachrrichards.CryptoAlerts.models;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;


@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Column(unique = true)
    @NotNull
    @Size(min = 5, message = "Please provide an e-mail")
    @Email(message = "Please provide a valid e-mail")
    private String email;

    private String password;

    @NotNull
    @Size(min = 1, max = 20, message = "First name must be between 1 and 20 characters")
    private String firstName;

    @NotNull
    @Size(min = 1, max = 20, message = "Last name must be between 1 and 20 characters")
    private String lastName;

    @OneToMany
    private List<Alert> alerts = new ArrayList<>();

    private boolean enabled;

    private String confirmationToken;
    private String[] role;

    public User() {}

    public User(String firstName, String lastName, String email, String password, boolean enabled, String[] role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        setPassword(password);
        this.enabled = enabled;
        this.role = role;
    }

    public int getId() { return id; }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = PASSWORD_ENCODER.encode(password);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public String[] getRole() {
        return role;
    }

    public void setRole(String[] role) {
        this.role = role;
    }

    public List<Alert> getAlerts() { return alerts; }

    public void addAlerts(Alert alert) { alerts.add(alert); }
}
