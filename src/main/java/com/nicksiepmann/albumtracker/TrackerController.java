/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nicksiepmann.albumtracker;

/**
 *
 * @author Nick.Siepmann
 */
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TrackerController {

    private final AlbumRepository albumRepository;
    private Album album;
    private GridBuilder gridBuilder;

    @Autowired
    public TrackerController(AlbumRepository albumRepository, GridBuilder gridBuilder) {
        this.albumRepository = albumRepository;
        this.gridBuilder = gridBuilder;
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
            model.addAttribute("grid", gridBuilder.buildGrid(this.album));
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
            Optional<Album> optAlbum = this.albumRepository.findById(Long.valueOf(id));
            if (optAlbum.isPresent() && !id.equals("")) {
                this.album = optAlbum.get();
            }
        }

        if (this.album != null) {
            model.addAttribute("album", this.album);
            model.addAttribute("grid", gridBuilder.buildGrid(this.album));
            return "index";
        }

        System.out.println("id was " + id);
        return "albums";
    }

    @GetMapping("/song") //get specified song from current album
    public String getSong(@RequestParam(value = "name") String name, Model model) {
        if (this.album != null) {
            model.addAttribute("song", this.album.getSong(name));
            model.addAttribute("grid", gridBuilder.buildGrid(this.album, true));
            return "song";
        }
        return "redirect:/albums";
    }

    @RequestMapping(value = "/task/{taskId}", method = RequestMethod.GET)
    public String getTask(@PathVariable String taskId, Model model) {
        ArrayList<Task> task = this.album.getSong(taskId.split("_")[1]).getTask(taskId.split("_")[2]).getTasks();
        model.addAttribute("task", task);
        model.addAttribute("songname",taskId.split("_")[1]);
        model.addAttribute("taskname",taskId.split("_")[2]);
//        System.out.println("refreshing data with " + model.getAttribute("task"));
        return "fragments::subtasktable";
    }

    @RequestMapping(value = "/subtaskstatus", method = RequestMethod.GET)
    public String setSubtaskStatus(@RequestParam(value = "songname") String songname, @RequestParam(value = "taskname") String taskname, @RequestParam(value = "subtaskname") String subtaskname, @RequestParam(value = "value") String value, Model model) {
        Task task = this.album.getSong(songname).getTask(taskname).getTask(subtaskname);
//        System.out.println("setting status of " + task.getName());
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
                if (this.album.getSong(songname).getTask(taskname).getTasks().size() > 0) {
                    this.album.getSong(songname).getTask(taskname).setDone(true); // if has task has subtasks and they're all done, set parent task to done
                }
                break;
        }
        this.albumRepository.save(album);

        model.addAttribute("task", task);
        return "redirect:/";
    }

    @RequestMapping("/new/album") //create new album
    public String newAlbum(@RequestParam(value = "name") String name, @RequestParam(value = "artist") String artist, Model model) {
        Album album = new Album(name, artist);
        this.albumRepository.save(album);
        model.addAttribute("album", album);
        this.album = album;
        return "redirect:/";
    }

    @RequestMapping("/rename/phase")
    public String renamePhase(@RequestParam(value = "phasenumber") String phasenumber, @RequestParam(value = "newname") String newname, Model model) {
//        System.out.println(phasenumber);
        if (this.album != null) {
            this.album.getIndex().getPhases().set(Integer.valueOf(phasenumber), newname);
            this.albumRepository.save(album);
            return "redirect:/tasks";
        }
        return "redirect:/albums";
    }

    @RequestMapping("/new/phase") //create new phase
    public String newPhase(@RequestParam(value = "name") String name, Model model) {
        if (this.album != null) {
            this.album.getIndex().getPhases().add(name);
            this.albumRepository.save(album);
            return "redirect:/tasks";
        }
        return "redirect:/albums";
    }

    @RequestMapping("/new/task") //create new task
    public String newTask(@RequestParam(value = "name") String name, Model model) {
        if (this.album != null) {
            this.album.getIndex().createTask(name);
            this.albumRepository.save(album);
            return "redirect:/tasks";
        }
        return "redirect:/albums";
    }

    @RequestMapping("/new/subtask") //create new task
    public String newSubtask(@RequestParam(value = "songname") String songname, @RequestParam(value = "taskname") String taskname, @RequestParam(value = "subtaskname") String subtaskname, Model model) {
        if (this.album != null) {
            Task task = this.album.getSong(songname).getTask(taskname);
            task.addTask(subtaskname);
            this.albumRepository.save(album);
            model.addAttribute("task", task);
            return "redirect:/";
        }
        return "redirect:/albums";
    }

    @RequestMapping("/new/song") //create new song
    public String newSong(@RequestParam(value = "name") String name, Model model) {
        if (this.album != null) {
            this.album.addSong(name);
            this.albumRepository.save(album);
            return "redirect:/";
        }
        return "redirect:/albums";
    }

    @RequestMapping("/reorder/task/up") //move task earlier in the order
    public String moveTaskUp(@RequestParam(value = "name") String name, Model model) {
        if (this.album != null) {
            this.album.getIndex().moveTask(name, false);
            this.albumRepository.save(album);
            return "redirect:/tasks";
        }
        return "redirect:/albums";
    }

    @RequestMapping("/reorder/task/down") //move task later in the order
    public String moveTaskDown(@RequestParam(value = "name") String name, Model model) {
        if (this.album != null) {
            this.album.getIndex().moveTask(name, true);
            this.albumRepository.save(album);
            return "redirect:/tasks";
        }
        return "redirect:/albums";
    }

    @RequestMapping("/task/setphase") //move task later in the order
    public String setTaskPhase(@RequestParam(value = "value") String value, Model model) {
        if (this.album != null) {
//            System.out.println("set task/phase" + value);
            this.album.getIndex().setTaskPhase(value.split("_")[0], Integer.valueOf(value.split("_")[1]));
            this.albumRepository.save(album);
            return "redirect:/tasks";
        }
        return "redirect:/albums";
    }

    @RequestMapping("/settaskstatus")
    public String setTaskStatus(@RequestParam(value = "songname") String songName, @RequestParam(value = "taskname") String taskName, @RequestParam(value = "value") String value, Model model) {
        if (this.album != null) {
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

            this.albumRepository.save(album);
            return "redirect:/";
        }
        return "redirect:/albums";
    }

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
//        System.out.println("deleting");
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
            this.albumRepository.save(album);
            return "redirect:/tasks";
        }
        return "redirect:/albums";
    }

    @RequestMapping("/delete/task") //set specified album as current album
    public String deleteTask(@RequestParam(value = "task") String task, Model model) {
        if (this.album != null) {
            this.album.getIndex().deleteTask(task);
            this.albumRepository.save(album);
            return "redirect:/tasks";
        }
        return "redirect:/albums";
    }

//    @DeleteMapping("/songs/{id}") //delete specified song
//
//    @DeleteMapping("/tasks/{id}") //delete specified task
}
