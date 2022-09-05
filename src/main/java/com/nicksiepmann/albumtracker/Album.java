/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nicksiepmann.albumtracker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;
import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import java.time.LocalDateTime;
import java.util.Collections;
import org.springframework.data.annotation.Id;

/**
 *
 * @author Nick.Siepmann
 */
@Entity(name = "albums")
public class Album {

    @Id
    Long id;
    private String name;
    private String artist;
    private String notes;
    private ArrayList<Song> songs;
    private Index index;
    private ArrayList<Comment> comments;

    public Album() {
        this.songs = new ArrayList<>();
        this.index = new Index();
        this.notes = "";
        this.name = "";
        this.artist = "";
        this.comments = new ArrayList<>();
    }

    public Album(String name, String artist) {
        this.name = name;
        this.artist = artist;
        this.notes = "";
        this.songs = new ArrayList<>();
        this.index = new Index();
        this.comments = new ArrayList<>();
    }

    public Album(String name, String artist, String notes, ArrayList<Song> songs, Index index, ArrayList<Comment> comments) {
        this.name = name;
        this.artist = artist;
        this.notes = notes;
        this.songs = songs;
        this.index = index;
        this.comments = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public Index getIndex() {
        return index;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
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

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public void setIndex(Index index) {
        this.index = index;
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

    public void moveSong(String name, boolean increase) {
        if (increase) {
            if (this.songs.indexOf(this.getSong(name)) < this.songs.size() - 1) {
                Collections.swap(songs, this.songs.indexOf(this.getSong(name)), this.songs.indexOf(this.getSong(name)) + 1);
            }
        } else {
            if (this.songs.indexOf(this.getSong(name)) > 0) {
                Collections.swap(songs, this.songs.indexOf(this.getSong(name)), this.songs.indexOf(this.getSong(name)) - 1);
            }
        }
    }

    public ArrayList<Comment> getComments() {
        return this.comments;
    }

    public void addComment(String commentText) {
        String userId = "defaultuser";
        String timestamp = LocalDateTime.now().toString();        
        this.comments.add(new Comment(userId, timestamp, commentText));
    }

    @Override
    public String toString() {
        return "Album{" + "id=" + this.id + "name=" + name + ", notes=" + notes + ", songs=" + songs + ", " + "taskIndex=" + index.getTaskIndex().keySet().stream().map(s -> s + " " + index.getTaskIndex().get(s)[0] + "/" + index.getTaskIndex().get(s)[1]).collect(Collectors.joining(";")) + '}';
    }

}
