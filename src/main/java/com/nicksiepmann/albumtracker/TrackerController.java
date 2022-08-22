/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nicksiepmann.albumtracker;

/**
 *
 * @author Nick.Siepmann
 */
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrackerController {

    private final AlbumRepository albumRepository;

    public TrackerController(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

//    //get
//    @GetMapping("/")
//
//    @GetMapping("/login")
//
    @GetMapping("/index")
    public String getIndex(){
        return "index";
    }

//    @GetMapping("/albums")
//
//    @GetMapping("/newalbum")
//
//    @GetMapping("/albums/{id}")
//
//    @GetMapping("/songs/{id}")
//
//    @GetMapping("/album/tasks")
//
//    @GetMapping("/album/notes")
//
//    @GetMapping("/error")
//
//    //post
//    @PostMapping("/newalbum")
//
//    @PostMapping("/newsong")
//
//    @PostMapping("/album/tasks")
//
//    @PostMapping("/save"
//
//    //delete
//    @DeleteMapping("/albums")
//
//    @DeleteMapping("/songs/{id}")
}
