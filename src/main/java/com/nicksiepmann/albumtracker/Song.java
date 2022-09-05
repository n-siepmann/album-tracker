/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nicksiepmann.albumtracker;

import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 *
 * @author Nick.Siepmann
 */
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void addNotes(String notes) {
        if (this.notes.isEmpty()) {
            setNotes(notes);
        } else {
            this.notes = this.notes + System.lineSeparator() + notes;
        }
    }
    
    public ArrayList<Comment> getComments(){
        return this.comments;
    }

    public void addComment(String commentText){
        String userId = "defaultuser";
        String timestamp = LocalDateTime.now().toString();        
        this.comments.add(new Comment(userId, timestamp, commentText));
    }
    
    @Override
    public String toString() {
        return "Song{" + "name=" + name + ", notes=" + notes + ", tasks=" + super.getTasks().toString() + '}';
//        return name + "~" + notes + "~" + super.getTasks().toString();

    }

}
