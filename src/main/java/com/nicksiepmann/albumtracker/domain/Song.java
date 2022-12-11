/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nicksiepmann.albumtracker.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import lombok.Data;

/**
 *
 * @author Nick.Siepmann
 */
@Data
public class Song extends TaskContainer {

    private String name;
    private String notes;
    private ArrayList<Comment> comments;

    public Song() {
        this.notes = "";
        this.comments = new ArrayList<>();
    }

    public Song(String name) {
        this.name = name;
        this.notes = "";
        this.comments = new ArrayList<>();
    }

    public Song(String name, String notes, ArrayList<Comment> comments) {
        this.name = name;
        this.notes = notes;
        this.comments = comments;
    }

    public void addNotes(String notes) {
        if (this.notes.isEmpty()) {
            setNotes(notes);
        } else {
            this.notes = this.notes + System.lineSeparator() + notes;
        }
    }

    public void addComment(String commentText, User user) {
//        String userId = "defaultuser";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String timestamp = LocalDateTime.now().format(formatter);
        this.comments.add(new Comment(user, timestamp, commentText));
    }

}
