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

    @RequestMapping("/rename/phase")
    public String renamePhase(@RequestParam(value = "phasenumber") String phasenumber, @RequestParam(value = "newname") String newname, Model model) {
        System.out.println(phasenumber);
        if (this.album != null) {
            this.album.getIndex().getPhases().set(Integer.valueOf(phasenumber), newname);
            this.album.getIndex().getPhases().stream().forEach(s -> System.out.println(s));
//            this.albumRepository.save(this.album);
            return "redirect:/tasks";
        }
        return "redirect:/albums";
    }

    @GetMapping("/index") //set specified album as current album
    public String getAlbum(@RequestParam(value = "id") String id, Model model) {
        if (this.albumRepository.count() > 0) {
            Optional<Album> optAlbum = this.albumRepository.findById(Long.valueOf(id));
            if (optAlbum.isPresent() && !id.equals("")) {
                this.album = optAlbum.get();
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
    @RequestMapping("/new/album") //create new album
    public String newAlbum(@RequestParam(value = "name") String name, @RequestParam(value = "artist") String artist, Model model) {
        Album album = new Album(name, artist);
        this.albumRepository.save(album);
        model.addAttribute("album", album);
        this.album = album;
        return "redirect:/";
    }

    @RequestMapping("/new/phase") //create new album
    public String newPhase(@RequestParam(value = "name") String name, Model model) {
        if (this.album != null) {
            this.album.getIndex().getPhases().add(name);
            return "redirect:/tasks";
        }
        return "redirect:/albums";
    }

    @RequestMapping("/new/task") //create new album
    public String newTask(@RequestParam(value = "name") String name, Model model) {
        if (this.album != null) {
            this.album.getIndex().createTask(name);
            return "redirect:/tasks";
        }
        return "redirect:/albums";
    }

    @RequestMapping("/reorder/task/up") //move task earlier in the order
    public String moveTaskUp(@RequestParam(value = "name") String name, Model model) {
        if (this.album != null) {
            this.album.getIndex().moveTask(name, false);
            return "redirect:/tasks";
        }
        return "redirect:/albums";
    }

    @RequestMapping("/reorder/task/down") //move task later in the order
    public String moveTaskDown(@RequestParam(value = "name") String name, Model model) {
        if (this.album != null) {
            this.album.getIndex().moveTask(name, true);
            return "redirect:/tasks";
        }
        return "redirect:/albums";
    }

    @RequestMapping("/task/setphase") //move task later in the order
    public String setTaskPhase(@RequestParam(value = "value") String value, Model model) {
        if (this.album != null) {
            System.out.println("set task/phase" + value);
            this.album.getIndex().setTaskPhase(value.split("_")[0], Integer.valueOf(value.split("_")[1]));
            return "redirect:/tasks";
        }
        return "redirect:/albums";
    }
//
//    @PostMapping("/newsong") //create new song in current album
//
// put
//    @PutMapping("/tasks") //update tasks for current album
//
//    @PutMapping("/songs/{id}/tasks") //update tasks for specified song from current album
//

    @RequestMapping("/albums/rename") //update name/notes for current album
    public String renameAlbum(@RequestParam(value = "id") String id, @RequestParam(value = "name") String name, @RequestParam(value = "artist") String artist, Model model) {
        if (this.albumRepository.count() > 0) {
            Optional<Album> optAlbum = this.albumRepository.findById(Long.valueOf(id));
            if (optAlbum.isPresent() && !id.equals("")) {
                Album album = optAlbum.get();
                album.setName(name);
                album.setArtist(artist);
                this.albumRepository.save(album);
                this.album = album;
                model.addAttribute("album", this.album);
                return "redirect:/";
            }
        }
        return "error";
    }
//    @PutMapping("/songs/{id}") //update name/notes for specified song from current album
//
//    //delete

    @RequestMapping("/delete/album") //set specified album as current album
    public String deleteAlbum(@RequestParam(value = "id") String id, Model model) {
        System.out.println("deleting");
        if (this.albumRepository.count() > 0) {
            this.albumRepository.deleteById(Long.valueOf(id));
            return "redirect:/albums";
        }
        System.out.println("delete failed: id was " + id);
        return "redirect:/albums";
    }

    @RequestMapping("/delete/phase") //set specified album as current album
    public String deletePhase(@RequestParam(value = "phasenumber") String phasenumber, Model model) {
        if (this.album != null) {
            this.album.getIndex().deletePhase(Integer.valueOf(phasenumber));
            return "redirect:/tasks";
        }
        return "redirect:/albums";
    }

    @RequestMapping("/delete/task") //set specified album as current album
    public String deleteTask(@RequestParam(value = "task") String task, Model model) {
        if (this.album != null) {
            this.album.getIndex().deleteTask(task);
            return "redirect:/tasks";
        }
        return "redirect:/albums";
    }

//    @DeleteMapping("/songs/{id}") //delete specified song
//
//    @DeleteMapping("/tasks/{id}") //delete specified task
}
