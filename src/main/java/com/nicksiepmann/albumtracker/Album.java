/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nicksiepmann.albumtracker;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 *
 * @author Nick.Siepmann
 */
public class Album {

    private String name;
    private String notes;
    private ArrayList<Song> songs;
    private Index index;

    public Album() {
        this.songs = new ArrayList<>();
        this.index = new Index();
    }

    public Album(String name) {
        this.name = name;
        this.songs = new ArrayList<>();
        this.index = new Index();
    }

    public Index getIndex() {
        return index;
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

    public void addSong(String name) {
        this.songs.add(new Song(name));
    }

    public void addTaskToSong(Song song, String name) {
        if (this.index.getTaskIndex().containsKey(name)) {
            song.addTask(name);
        } else {
            System.out.println("task not found in index");
        }
    }

    @Override
    public String toString() {
        return "Album{" + "name=" + name + ", notes=" + notes + ", songs=" + songs + ", " + "taskIndex=" + index.getTaskIndex().keySet().stream().map(s -> s + " " + index.getTaskIndex().get(s)[0] + "/" + index.getTaskIndex().get(s)[1]).collect(Collectors.joining(";")) + '}';
    }

}
