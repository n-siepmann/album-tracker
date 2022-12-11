/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nicksiepmann.albumtracker;

import com.nicksiepmann.albumtracker.domain.Album;
import com.nicksiepmann.albumtracker.domain.AlbumRepository;
import com.nicksiepmann.albumtracker.domain.GridBuilder;
import com.nicksiepmann.albumtracker.domain.Task;
import com.nicksiepmann.albumtracker.domain.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 *
 * @author Nick.Siepmann
 */
@Service
@Getter
public class TrackerService {

    private final AlbumRepository albumRepository;
    @Setter
    private Album album;
    private final GridBuilder gridBuilder;
    private User user;
    private final Emailer emailer;
    @Value("${mailjet.secret}")
    private String secret;

    public TrackerService(AlbumRepository albumRepository, GridBuilder gridBuilder, Emailer emailer) {
        this.albumRepository = albumRepository;
        this.gridBuilder = gridBuilder;
        this.emailer = emailer;
        this.album = null;
        this.user = null;
    }

    void newUser(OAuth2User principal) {
        this.user = new User(principal.getAttribute("name"), principal.getAttribute("email"));
    }

    public String cleanString(String input) { //only required for Songs and Tasks because those strings may include delimiters
        String output = input.trim().replace(" ", "_").replace("̪QQQ", "");
        return output;
    }

    List<Album> listMyAlbums() {
        return StreamSupport.stream(this.albumRepository.findAll().spliterator(), false).filter(s -> s.getEditors().contains(this.user)).toList();
    }

    Album findAlbumById(long id) {
        Optional<Album> optAlbum = this.albumRepository.findById(id);
        if (optAlbum.isPresent()) {
            return optAlbum.get();
        }
        return null;
    }

    void setAlbumById(String id) {
        if (!id.equals("")) {
            Album foundAlbum = findAlbumById(Long.valueOf(id));
            if (!Objects.isNull(foundAlbum)) {
                this.album = foundAlbum;
            }
        }
    }

    void updateWithCurrentEditor() {
        this.album.updateEditors(this.user);
    }

    String[][] getAlbumGrid() {
        return gridBuilder.buildGrid(this.album);
    }

    String[][] getSongGrid(String name) {
        return gridBuilder.buildGrid(this.album, true, name);
    }

    ArrayList<Task> getSubtasks(String taskId) {
        return this.album.getSong(taskId.split("̪QQQ")[1]).getTask(taskId.split("̪QQQ")[2]).getTasks();
    }

    boolean albumExists(String name, String artist) {
        return !this.albumRepository.findByNameAndArtist(name, artist).isEmpty();
    }

    Album newAlbum(String name, String artist) {
        Album newalbum = new Album(name, artist, this.user);
        this.albumRepository.save(newalbum);
        this.album = newalbum;
        return this.album;
    }

    void renamePhase(Integer phasenumber, String newname) {
        this.album.getIndex().getPhases().set(phasenumber, newname);
        this.albumRepository.save(this.album);
    }

    void renameSong(String oldname, String newname) {
        this.album.getSong(oldname).setName(cleanString(newname));
        this.albumRepository.save(this.album);
    }

    void renameAlbum(long id, String name, String artist) {
        Album foundalbum = findAlbumById(id);
        foundalbum.setName(name);
        foundalbum.setArtist(artist);
        this.albumRepository.save(foundalbum);
        this.album = foundalbum;
    }

    void addPhase(String name) {
        this.album.getIndex().getPhases().add(name);
        this.albumRepository.save(this.album);
    }

    void newTask(String name) {
        this.album.getIndex().createTask(cleanString(name));
        this.albumRepository.save(this.album);
    }

    void addSubtask(String songname, String taskname, String subtaskname) {
        Task task = this.album.getSong(songname).getTask(taskname);
        task.addTask(cleanString(subtaskname));
        this.albumRepository.save(this.album);
    }

    void addSong(String name) {
        this.album.addSong(cleanString(name));
        this.albumRepository.save(this.album);
    }

    void addEditor(User user) {
        this.album.addEditor(user);
        this.albumRepository.save(this.album);
    }

    void removeEditor(User user) {
        this.album.removeEditor(user);
        this.albumRepository.save(this.album);
    }

    void moveSong(String name, boolean increase) {
        this.album.moveSong(name, increase);
        this.albumRepository.save(this.album);
    }

    void moveTask(String name, boolean increase) {
        this.album.getIndex().moveTask(name, increase);
        this.albumRepository.save(this.album);
    }

    void setTaskPhase(String value) {
        this.album.getIndex().setTaskPhase(value.split("̪QQQ")[0], Integer.valueOf(value.split("̪QQQ")[1]));
        this.albumRepository.save(this.album);
    }

    void setTaskStatus(String songName, String taskName, String value) {
        
        switch (value) {
            case "complete":
                this.album.getSong(songName).getTask(taskName).setDone(true);
                break;
            case "incomplete":
                this.album.getSong(songName).getTask(taskName).setDone(false);
                break;
            case "add":
                this.album.getSong(songName).addTask(taskName);
                break;
            case "delete":
                this.album.getSong(songName).removeTask(taskName);
                break;
        }

        this.albumRepository.save(this.album);
    }

    void setSubtaskStatus(String songname, String taskname, String subtaskname, String value) {
        Task task = this.album.getSong(songname).getTask(taskname).getTask(subtaskname);

        switch (value) {
            case "complete":
                task.setDone(true);
                this.album.getSong(songname).getTask(taskname).setDone(true); // if all subtasks done, set parent task to done
                break;
            case "incomplete":
                task.setDone(false);
                break;
            case "delete":
                this.album.getSong(songname).getTask(taskname).removeTask(subtaskname);
                if (this.album.getSong(songname).getTask(taskname).getTasks().isEmpty()) {
                    this.album.getSong(songname).getTask(taskname).setDone(true); // if has task has subtasks and they're all done, set parent task to done
                }
                break;
        }
        this.albumRepository.save(this.album);
    }

    void addSongComment(String songName, String commentText) {
        this.album.getSong(songName).addComment(commentText, this.user);
        this.albumRepository.save(this.album);
    }

    void addAlbumComment(String commentText) {
        this.album.addComment(commentText, this.user);
        this.albumRepository.save(this.album);
    }

    void setSongNotes(String songName, String notesText) {
        this.album.getSong(songName).setNotes(notesText);
        this.albumRepository.save(this.album);
    }

    void setAlbumNotes(String notesText) {
        this.album.setNotes(notesText);
        this.albumRepository.save(this.album);
    }

    void deleteSong(String song) {
        this.album.getSongs().remove(this.album.getSong(song));
        this.albumRepository.save(this.album);
    }

    void deletePhase(int phasenumber) {
        this.album.getIndex().deletePhase(phasenumber);
        this.albumRepository.save(this.album);
    }

    void deleteTask(String task) {
        this.album.getIndex().deleteTask(task);
        this.albumRepository.save(this.album);
    }

}
