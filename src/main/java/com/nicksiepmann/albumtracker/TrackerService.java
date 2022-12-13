/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nicksiepmann.albumtracker;

import com.nicksiepmann.albumtracker.domain.Album;
import com.nicksiepmann.albumtracker.domain.AlbumRepository;
import com.nicksiepmann.albumtracker.domain.GridBuilder;
import com.nicksiepmann.albumtracker.domain.Song;
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

    Album findAlbumById(long id) throws ATException {
        if (this.albumRepository.count() == 0) {
            throw new ATException("No albums found");
        }
        Optional<Album> optAlbum = this.albumRepository.findById(id);
        if (optAlbum.isPresent()) {
            return optAlbum.get();
        }
        throw new ATException("Album not found");
    }

    void setAlbumById(String id) throws ATException {
        if (!id.equals("")) {
            Album foundAlbum = findAlbumById(Long.valueOf(id));
            this.album = foundAlbum;
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

    Album newAlbum(String name, String artist) throws ATException {
        if (albumExists(name, artist)) {
            throw new ATException("An album with this name and artist already exists!");
        }
        Album newalbum = new Album(name, artist, this.user);
        this.albumRepository.save(newalbum);
        this.album = newalbum;
        return this.album;
    }

    void renamePhase(Integer phasenumber, String newname) throws ATException {
        if (this.album.getIndex().getPhases().contains(newname)) {
            throw new ATException("A phase by this name already exists!");
        }
        this.album.getIndex().getPhases().set(phasenumber, newname);
        this.albumRepository.save(this.album);
    }

    void renameSong(String oldname, String newname) throws ATException {
        if (this.album.getSongs().contains(new Song(cleanString(newname)))) {
            throw new ATException("A song by this name already exists!");
        }
        this.album.getSong(oldname).setName(cleanString(newname));
        this.albumRepository.save(this.album);
    }

    void renameAlbum(long id, String name, String artist) throws ATException {
        Album foundalbum = findAlbumById(id);
        if (albumExists(name, artist)) {
            throw new ATException("An album with this name and artist already exists!");
        }
        foundalbum.setName(name);
        foundalbum.setArtist(artist);
        this.albumRepository.save(foundalbum);
        this.album = foundalbum;
    }

    void addPhase(String name) throws ATException {
        if (this.album.getIndex().getPhases().contains(name)) {
            throw new ATException("A phase by this name already exists!");
        }
        this.album.getIndex().getPhases().add(name);
        this.albumRepository.save(this.album);
    }

    void newTask(String name) throws ATException {
        if (this.album.getIndex().getTaskIndex().containsKey(name)) {
            throw new ATException("A task by this name already exists!");
        }
        this.album.getIndex().createTask(cleanString(name));
        this.albumRepository.save(this.album);
    }

    void addSubtask(String songname, Task task, String subtaskname) throws ATException {
        if (task.getTasks().contains(new Task(subtaskname))) {
            throw new ATException("A subtask by this name already exists!");
        }
        task.addTask(cleanString(subtaskname));
        this.albumRepository.save(this.album);
    }

    void addSong(String name) throws ATException {
        if (this.album.getSongs().contains(new Song(name))) {
            throw new ATException("A song by this name already exists!");
        }
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

    void deleteSong(String song) throws ATException {
        if (!this.album.getOwner().equals(this.getUser())) {
            throw new ATException("Only the album owner can delete songs!");
        }
        this.album.getSongs().remove(this.album.getSong(song));
        this.albumRepository.save(this.album);
    }

    void deletePhase(int phasenumber) throws ATException {
        if (!this.album.getOwner().equals(this.getUser())) {
            throw new ATException("Only the album owner can delete phases!");
        }
        this.album.getIndex().deletePhase(phasenumber);
        this.albumRepository.save(this.album);
    }

    void deleteTask(String task) throws ATException {
        if (!this.album.getOwner().equals(this.getUser())) {
            throw new ATException("Only the album owner can delete tasks!");
        }
        this.album.getIndex().deleteTask(task);
        this.albumRepository.save(this.album);
    }

    void deleteAlbum(Long id) throws ATException {
        Album toDelete = findAlbumById(id);
        if (!toDelete.getOwner().equals(this.getUser())) {
            throw new ATException("Only the owner can delete the album!");
        }
        this.getAlbumRepository().deleteById(id);
    }

}
