/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nicksiepmann.albumtracker;

import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import java.util.ArrayList;

/**
 *
 * @author Nick.Siepmann
 */
public class Song extends TaskContainer {

    private String name;
    private String notes;

    public Song() {
        this.notes = "";
    }

    public Song(String name) {
        this.name = name;
        this.notes = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = cleanString(name);
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = cleanString(notes);
    }

    public void addNotes(String notes) {
        if (this.notes.isEmpty()) {
            setNotes(notes);
        } else {
            this.notes = this.notes + System.lineSeparator() + notes;
        }
    }

    @Override
    public String toString() {
        return "Song{" + "name=" + name + ", notes=" + notes + ", tasks=" + super.getTasks().toString() + '}';
//        return name + "~" + notes + "~" + super.getTasks().toString();

    }

}
