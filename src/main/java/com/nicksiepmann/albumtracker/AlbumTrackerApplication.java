package com.nicksiepmann.albumtracker;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@SpringBootApplication
public class AlbumTrackerApplication {

    @Autowired
    AlbumRepository albumRepository;

    public static void main(String[] args) {
        SpringApplication.run(AlbumTrackerApplication.class, args);
    }

    @ShellMethod("Saves an album: save-album <name> <artist>")
    public String saveAlbum(String name, String artist) {
        Album savedAlbum = this.albumRepository.save(new Album(name, artist));
        return savedAlbum.toString();
    }

//    @ShellMethod("Saves an album object")
//    public String saveAlbum(Album album) {
//        Album savedAlbum = this.albumRepository.save(album);
//        return savedAlbum.toString();
//    }
    @ShellMethod
    public String saveAlbumTest() {
        Album album = new Album("name", "artist");
        Album savedAlbum = this.albumRepository.save(album);
        System.out.println(savedAlbum.toString());

        album.addNotes("NotesTest");
        savedAlbum = this.albumRepository.save(album);
        System.out.println(savedAlbum.toString());

        album.addSong("SONG");
        album.addSong("MORESONG");
        album.addSong("THIRDSONG");
        Song song = album.getSong("SONG");
        album.getIndex().addPhase("PHASE");
        album.getIndex().createTask("TASK");
        System.out.println(album.toString());
        album.addTaskToSong(song, "TASK");
        album.getIndex().createTask("ANOTHER");
        album.getIndex().createTask("YETANOTHER");
        album.getIndex().createTask("YETANOTHERSTILL");
        album.addTaskToSong(song, "ANOTHER");
        System.out.println(album.toString());
        album.getIndex().addPhase("PHASE2");
        album.getIndex().createTask("NEWPHASETASK");
        album.getIndex().setTaskPhase("NEWPHASETASK", 1);
        album.addTaskToSong(song, "NEWPHASETASK");
        album.getIndex().createTask("NEWPHASETASK2");
        album.getIndex().addPhase("PHASE3");
        album.addTaskToSong(album.getSong("MORESONG"), "NEWPHASETASK");
        song.getTasks().get(1).addTask("SUBTASK");
        System.out.println("saving");
        savedAlbum = this.albumRepository.save(album);
        System.out.println("saved");

        return savedAlbum.toString();
    }

    @ShellMethod("Get all albums")
    public String findAllAlbums() {
        Iterable<Album> albums = this.albumRepository.findAll();
        return Lists.newArrayList(albums).toString();
    }

    @ShellMethod("Get album by name")
    public String findAlbumByName(String name) {
        List<Album> foundAlbum = this.albumRepository.findByName(name);
        return foundAlbum.toString();
    }

    @ShellMethod("Get album by artist")
    public String findAlbumByArtist(String artist) {
        List<Album> foundAlbum = this.albumRepository.findByArtist(artist);
        return foundAlbum.toString();
    }

    @ShellMethod("Get album by id")
    public String findAlbumById(Long id) {
        Optional<Album> foundAlbum = this.albumRepository.findById(id);
        return foundAlbum.toString();
    }

    @ShellMethod("Remove al albums")
    public void removeAllAlbums() {
        this.albumRepository.deleteAll();
    }

}
