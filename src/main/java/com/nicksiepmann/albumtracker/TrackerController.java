/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nicksiepmann.albumtracker;

/**
 *
 * @author Nick.Siepmann
 */
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TrackerController {

    private final AlbumRepository albumRepository;
    private Album album;

    @Autowired
    public TrackerController(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
        this.album = null;
    }

//    //get
//    @GetMapping("/")
//
    @GetMapping("/login")
    public String logIn() {
        return "login";
    }

    @GetMapping("/logout")
    public String logOut() {
        return "redirect:/login";
    }

    @GetMapping("/")
    public String getIndex(Model model) {
        if (this.album != null) {
            model.addAttribute("album", this.album);
            return "index";
        }
        return "redirect:/albums";
    }

    @GetMapping("/albums") //get list of albums
    public String getAlbumList(Model model) {
        this.album = null;
        if (this.albumRepository.count() > 0) {
            model.addAttribute("albums", this.albumRepository.findAll());
        }
        return "albums";
    }

    @GetMapping("/edit")
    public String getEditPage(Model model) {
        if (this.album != null) {
            model.addAttribute("album", this.album);
            return "edit";
        }
        return "redirect:/albums";
    }

    @GetMapping("/tasks")
    public String getTasksPage(Model model) {
        if (this.album != null) {
            model.addAttribute("album", this.album);
            model.addAttribute("tasks", this.album.getIndex().getTasksAndPhases());
            model.addAttribute("phases", this.album.getIndex().getPhases());
            return "tasks";
        }
        return "redirect:/albums";
    }

    @GetMapping("/index") //set specified album as current album
    public String getAlbum(@RequestParam(value = "id") String id, Model model) {
        if (this.albumRepository.count() > 0) {
            Optional<Album> album = this.albumRepository.findById(Long.valueOf(id));
            if (album.isPresent() && !id.equals("")) {
                this.album = album.get();
                model.addAttribute("album", this.album);
                return "index";
            }
        }
        System.out.println("id was " + id);
        return "albums";
    }

//    @GetMapping("/songs/{id}") //get specified song from current album
//
//    @GetMapping("/tasks") //view task manager for current album
//
//    @GetMapping("/edit")  //view edit page for current album
//
    @GetMapping("/error")
    public String error() {
        //add an error message
        return "redirect:/";
    }
//    //post

    @RequestMapping("/newalbum") //create new album
    public String newAlbum(@RequestParam(value = "name") String name, @RequestParam(value = "artist") String artist, Model model) {
        Album album = new Album(name, artist);
        this.albumRepository.save(album);
        model.addAttribute("album", album);
        this.album = album;
        return "redirect:/";
    }

//
//    @PostMapping("/newsong") //create new song in current album
//
// put
//    @PutMapping("/tasks") //update tasks for current album
//
//    @PutMapping("/songs/{id}/tasks") //update tasks for specified song from current album
//
//    @PutMapping("/albums/{id}") //update name/notes for current album
//
//    @PutMapping("/songs/{id}") //update name/notes for specified song from current album
//
//    //delete
    @RequestMapping("/delete") //set specified album as current album
    public String deleteAlbum(@RequestParam(value = "id") String id, Model model) {
        System.out.println("deleting");
        if (this.albumRepository.count() > 0) {
            this.albumRepository.deleteById(Long.valueOf(id));
            return "redirect:/albums";
        }
        System.out.println("delete failed: id was " + id);
        return "redirect:/albums";
    }
}

//    @DeleteMapping("/songs/{id}") //delete specified song
//
//    @DeleteMapping("/tasks/{id}") //delete specified task

