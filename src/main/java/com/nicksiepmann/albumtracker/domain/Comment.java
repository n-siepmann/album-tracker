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
public class Comment {

    private User user;
    private String timestamp;
    private String commentText;

    public Comment(User user, String timestamp, String commentText) {
        this.user = user;
        this.timestamp = timestamp;
        this.commentText = commentText;
    }

    public Comment() {
        this.user = null;
        this.timestamp = "";
        this.commentText = "";
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.user.getEmail());
        hash = 97 * hash + Objects.hashCode(this.timestamp);
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
        final Comment other = (Comment) obj;
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        if (!Objects.equals(this.commentText, other.commentText)) {
            return false;
        }
        return Objects.equals(this.timestamp, other.timestamp);
    }

}
