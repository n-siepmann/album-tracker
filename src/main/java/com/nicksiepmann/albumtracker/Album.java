/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nicksiepmann.albumtracker;

import java.util.ArrayList;

/**
 *
 * @author Nick.Siepmann
 */
public class Album {

    private String name;
    private String notes;
    private ArrayList<Song> songs;
    private ArrayList<Phase> phases;

    public Album() {
        this.songs = new ArrayList<>();
        this.phases = new ArrayList<>();
    }

    public Album(String name) {
        this.name = name;
        this.songs = new ArrayList<>();
        this.phases = new ArrayList<>();
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

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public Song getSong(String name) {
        for (Song song : this.songs) {
            if (song.getName().equals(name)) {
                return song;
            }
        }
        return null;
    }

    public void addSong(Song song) {
        this.songs.add(song);
    }

    public ArrayList<Phase> getPhases() {
        return phases;
    }

    public void addPhases(Phase phase) {
        this.phases.add(phase);
    }

    @Override
    public String toString() {
        return "Album{" + "name=" + name + ", notes=" + notes + ", songs=" + songs + ", phases=" + phases + '}';
    }

}
