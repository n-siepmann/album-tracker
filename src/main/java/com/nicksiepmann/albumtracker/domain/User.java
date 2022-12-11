/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nicksiepmann.albumtracker.domain;

import java.util.Objects;
import lombok.Data;

/**
 *
 * @author Nick.Siepmann
 */
@Data
public class User {

    private String name;
    private final String email;
    private boolean owner;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        this.owner = false;
    }

    public User(String email) {
        this.name = "";
        this.email = email;
        this.owner = false;
    }

    public User() {
        this.name = "";
        this.email = "";
        this.owner = false;
    }

    public User(String name, String email, boolean owner) {
        this.name = name;
        this.email = email;
        this.owner = owner;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.email);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        return Objects.equals(this.email, other.email);
    }

}
