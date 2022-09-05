/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nicksiepmann.albumtracker;

import java.util.Objects;

/**
 *
 * @author Nick.Siepmann
 */
public class Comment {

    private String userId;
    private String timestamp;
    private String commentText;

    public Comment(String userId, String timestamp, String commentText) {
        this.userId = userId;
        this.timestamp = timestamp;
        this.commentText = commentText;
    }

    public Comment() {
        this.userId = "";
        this.timestamp = "";
        this.commentText = "";
    }

    @Override
    public String toString() {
        return "Comment{" + "userId=" + userId + ", timestamp=" + timestamp + ", commentText=" + commentText + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.userId);
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
        if (!Objects.equals(this.userId, other.userId)) {
            return false;
        }
        if (!Objects.equals(this.commentText, other.commentText)) {
            return false;
        }
        return Objects.equals(this.timestamp, other.timestamp);
    }

    public String getUserId() {
        return userId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getCommentText() {
        return commentText;
    }

}
