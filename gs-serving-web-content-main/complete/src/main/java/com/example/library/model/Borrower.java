package com.example.library.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Borrower {
    @Id
    private String id;
    private String name;
    private String phone;

    public Borrower() {}

    public Borrower(String id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    // Getters và Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
