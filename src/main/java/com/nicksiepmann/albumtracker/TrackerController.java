/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nicksiepmann.albumtracker;

/**
 *
 * @author Nick.Siepmann
 */
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import com.nicksiepmann.albumtracker.domain.Song;
import com.nicksiepmann.albumtracker.domain.User;
import com.nicksiepmann.albumtracker.domain.Task;
import com.nicksiepmann.albumtracker.domain.Album;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TrackerController {

    private AlbumService service;

    @Autowired
    public TrackerController(AlbumService service) {
        this.service = service;
    }

    @GetMapping("/welcome")
    public String welcome() {
//        System.out.println(this.secret);
        return "welcome";
    }

    @GetMapping("/")
    public String getIndex(Model model, @AuthenticationPrincipal OAuth2User principal) {
        this.service.newUser(principal);

        if (this.service.getAlbum() != null) {
            model.addAttribute("album", this.service.getAlbum());
            if (!this.service.getAlbum().getSongs().isEmpty()) {
                model.addAttribute("grid", this.service.getGridBuilder().buildGrid(this.service.getAlbum()));
            }
            model.addAttribute("nosongs", this.service.getAlbum().getSongs().isEmpty());
            model.addAttribute("notasks", this.service.getAlbum().getIndex().getTaskIndex().keySet().isEmpty() | this.service.getAlbum().getIndex().getPhases().isEmpty());
            return "index";
        }
        return "redirect:/albums";
    }

    @GetMapping("/albums") //get list of albums
    public String getAlbumList(Model model) {
        this.service.setAlbum(null);
        if (this.service.getAlbumRepository().count() > 0) {
            List<Album> myAlbums = this.service.listMyAlbums();
            model.addAttribute("albums", myAlbums);
        }
        return "albums";
    }

    @GetMapping("/edit")
    public String getEditPage(Model model) {
        if (this.service.getAlbum() != null) {
            model.addAttribute("album", this.service.getAlbum());
            return "edit";
        }
        return "redirect:/albums";
    }

    @GetMapping("/tasks")
    public String getTasksPage(Model model) {
        if (this.service.getAlbum() != null) {
            model.addAttribute("album", this.service.getAlbum());
            model.addAttribute("tasks", this.service.getAlbum().getIndex().getTasksAndPhases());
            model.addAttribute("phases", this.service.getAlbum().getIndex().getPhases());
            model.addAttribute("notasks", this.service.getAlbum().getIndex().getTaskIndex().keySet().isEmpty());
            model.addAttribute("nophases", this.service.getAlbum().getIndex().getPhases().isEmpty());

            return "tasks";
        }
        return "redirect:/albums";
    }

    @GetMapping("/index") //set specified album as current album
    public String getAlbum(@RequestParam(value = "id") String id, Model model) {
        if (this.service.getAlbumRepository().count() > 0) {
            this.service.setAlbumById(id);
        }

        if (this.service.getAlbum() != null) {
            this.service.updateWithCurrentEditor();
            model.addAttribute("album", this.service.getAlbum());
            if (!this.service.getAlbum().getSongs().isEmpty()) {
                model.addAttribute("grid", this.service.getAlbumGrid());
            }
            model.addAttribute("nosongs", this.service.getAlbum().getSongs().isEmpty());
            model.addAttribute("notasks", this.service.getAlbum().getIndex().getTaskIndex().keySet().isEmpty() | this.service.getAlbum().getIndex().getPhases().isEmpty());
            return "index";
        }

//        System.out.println("id was " + id);
        return "albums";
    }

    @GetMapping("/song") //get specified song from current album
    public String getSong(@RequestParam(value = "name") String name, Model model) {
        if (this.service.getAlbum() != null) {
            model.addAttribute("song", this.service.getAlbum().getSong(name));
            model.addAttribute("grid", this.service.getSongGrid(name));
            model.addAttribute("notasks", this.service.getAlbum().getIndex().getTaskIndex().keySet().isEmpty() | this.service.getAlbum().getIndex().getPhases().isEmpty());
            return "song";
        }
        return "redirect:/albums";
    }

    @RequestMapping(value = "/task/{taskId}", method = RequestMethod.GET)
    public String getTask(@PathVariable String taskId, Model model) {
        ArrayList<Task> task = this.service.getTask(taskId);
        model.addAttribute("task", task);
        model.addAttribute("songname", taskId.split("̪QQQ")[1]);
        model.addAttribute("taskname", taskId.split("̪QQQ")[2]);
//        System.out.println("refreshing data with " + model.getAttribute("task"));
        return "fragments::subtasktable";
    }

    @RequestMapping(value = "/taskforsong/{taskId}", method = RequestMethod.GET)
    public String getTaskForSong(@PathVariable String taskId, Model model) {
        ArrayList<Task> task = this.service.getTask(taskId);
        model.addAttribute("task", task);
        model.addAttribute("songname", taskId.split("̪QQQ")[1]);
        model.addAttribute("taskname", taskId.split("̪QQQ")[2]);
//        System.out.println("refreshing data with " + model.getAttribute("task"));
        return "fragments::subtasktablesong";
    }

    @RequestMapping("/new/album") //create new album
    public String newAlbum(@RequestParam(value = "name") String name, @RequestParam(value = "artist") String artist, Model model) {
        name = this.service.cleanString(name);
        artist = this.service.cleanString(artist);

        if (this.service.albumExists(name, artist)) {
            model.addAttribute("error", "An album with this name and artist already exists!");
            return "error";
        }

        Album newalbum = this.service.newAlbum(name, artist);
        model.addAttribute("album", newalbum);
        return "redirect:/";
    }

    @RequestMapping("/rename/phase")
    public String renamePhase(@RequestParam(value = "phasenumber") String phasenumber, @RequestParam(value = "newname") String newname, Model model) {
//        System.out.println(phasenumber);
        newname = this.service.cleanString(newname);

        if (this.service.getAlbum().getIndex().getPhases().contains(newname)) {
            model.addAttribute("error", "A phase by this name already exists!");
            return "error";
        }
        if (this.service.getAlbum() != null) {
            this.service.renamePhase(Integer.valueOf(phasenumber), newname);
            return "redirect:/tasks";
        }
        return "redirect:/albums";
    }

    @RequestMapping("/rename/song")
    public String renameSong(@RequestParam(value = "oldname") String oldname, @RequestParam(value = "newname") String newname, @RequestParam(value = "returnto") String returnto, Model model) {
//        System.out.println(phasenumber);
        newname = this.service.cleanString(newname);

        if (this.service.getAlbum().getSongs().contains(new Song(newname))) {
            model.addAttribute("error", "A song by this name already exists!");
            return "error";
        }

        if (this.service.getAlbum() != null) {
            this.service.renameSong(oldname, newname);

            String returnString = "redirect:/" + returnto.trim();
            returnString = returnString.replace("//", "/").replace("index", "");
            return returnString;
        }
        return "redirect:/albums";
    }

    @RequestMapping("/albums/rename") //update name/notes for current album
    public String renameAlbum(@RequestParam(value = "id") String id, @RequestParam(value = "name") String name, @RequestParam(value = "artist") String artist, @RequestParam(value = "returnto") String returnto, Model model) {
        if (this.service.getAlbumRepository().count() > 0) {

            if (!this.service.getAlbumRepository().findByNameAndArtist(name, artist).isEmpty()) {
                model.addAttribute("error", "An album with this name and artist already exists!");
                return "error";
            }
            Album foundAlbum = this.service.findAlbumById(Long.valueOf(id));
            if (Objects.isNull(foundAlbum) || id.equals("")) {
                model.addAttribute("error", "Album not found");
                return "error";
            }
            this.service.renameAlbum(Long.valueOf(id), this.service.cleanString(name), this.service.cleanString(artist));

            model.addAttribute("album", this.service.getAlbum());
            String returnString = "redirect:/" + returnto.trim();
            returnString = returnString.replace("//", "/").replace("index", "");
            return returnString;
        }
        return "error";
    }

    @RequestMapping("/new/phase") //create new phase
    public String newPhase(@RequestParam(value = "name") String name, Model model) {
        name = this.service.cleanString(name);

        if (this.service.getAlbum().getIndex().getPhases().contains(name)) {
            model.addAttribute("error", "A phase by this name already exists!");
            return "error";
        }

        if (this.service.getAlbum() != null) {
            this.service.addPhase(name);
            return "redirect:/tasks";
        }
        return "redirect:/albums";
    }

    @RequestMapping("/new/task") //create new task
    public String newTask(@RequestParam(value = "name") String name, Model model) {
        name = this.service.cleanString(name);

        if (this.service.getAlbum().getIndex().getTaskIndex().containsKey(name)) {
            model.addAttribute("error", "A task by this name already exists!");
            return "error";
        }

        if (this.service.getAlbum() != null) {
            this.service.newTask(name);
            return "redirect:/tasks";
        }
        return "redirect:/albums";
    }

    @RequestMapping("/new/subtask") //create new task
    public String newSubtask(@RequestParam(value = "songname") String songname, @RequestParam(value = "taskname") String taskname, @RequestParam(value = "subtaskname") String subtaskname, @RequestParam(value = "returnto") String returnto, Model model) {
        subtaskname = this.service.cleanString(subtaskname);

        if (this.service.getAlbum() != null) {

            Task task = this.service.getAlbum().getSong(songname).getTask(taskname);

            if (task.getTasks().contains(new Task(subtaskname))) {
                model.addAttribute("error", "A subtask by this name already exists!");
                return "error";
            }

            this.service.addSubtask(songname, taskname, subtaskname);

            model.addAttribute("task", task);
            String returnString = "redirect:/" + returnto.trim();
            returnString = returnString.replace("//", "/").replace("index", "");
            return returnString;
        }
        return "redirect:/albums";
    }

    @RequestMapping("/new/song") //create new song
    public String newSong(@RequestParam(value = "name") String name, @RequestParam(value = "returnto") String returnto, Model model) {
        name = this.service.cleanString(name);

        if (this.service.getAlbum().getSongs().contains(new Song(name))) {
            model.addAttribute("error", "A song by this name already exists!");
            return "error";
        }

        if (this.service.getAlbum() != null) {
            this.service.addSong(name);

            String returnString = "redirect:/" + returnto.trim();
            returnString = returnString.replace("//", "/").replace("index", "");
            return returnString;
        }
        return "redirect:/albums";
    }

    @RequestMapping("/editors/add") //add editors
    public String addEditor(@RequestParam(value = "email") String email, @RequestParam(value = "returnto") String returnto, Model model) {
        if (this.service.getAlbum() != null) {

            System.out.println("sending email");
            try {
                System.out.println(this.service.getEmailer().SendInvite(email, this.service.getUser().getName().replace("_", " ")).toString());
            } catch (MailjetException ex) {
                Logger.getLogger(TrackerController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MailjetSocketTimeoutException ex) {
                Logger.getLogger(TrackerController.class.getName()).log(Level.SEVERE, null, ex);
            }

            this.service.addEditor(new User(email));

            String returnString = "redirect:/" + returnto.trim();
            returnString = returnString.replace("//", "/").replace("index", "");
            return returnString;
        }
        return "redirect:/albums";
    }

    @RequestMapping("/editors/delete") //add editors
    public String deleteEditor(@RequestParam(value = "email") String email, @RequestParam(value = "returnto") String returnto, Model model) {
        if (this.service.getAlbum() != null) {
            this.service.removeEditor(new User(email));

            if (email.equals(this.service.getUser().getEmail())) {
                return "redirect:/albums";
            }

            String returnString = "redirect:/" + returnto.trim();
            returnString = returnString.replace("//", "/").replace("index", "");
            return returnString;
        }
        return "redirect:/albums";
    }

    @RequestMapping("/reorder/song/up") //move song earlier in the order
    public String moveSongUp(@RequestParam(value = "name") String name, @RequestParam(value = "returnto") String returnto, Model model) {
        if (this.service.getAlbum() != null) {
            this.service.moveSong(name, false);

            String returnString = "redirect:/" + returnto.trim();
            returnString = returnString.replace("//", "/").replace("index", "");
            return returnString;
        }
        return "redirect:/albums";
    }

    @RequestMapping("/reorder/song/down") //move song later in the order
    public String moveSongDown(@RequestParam(value = "name") String name, @RequestParam(value = "returnto") String returnto, Model model) {
        if (this.service.getAlbum() != null) {
            this.service.moveSong(name, true);
            String returnString = "redirect:/" + returnto.trim();
            returnString = returnString.replace("//", "/").replace("index", "");
            return returnString;
        }
        return "redirect:/albums";
    }

    @RequestMapping("/reorder/task/up") //move task earlier in the order
    public String moveTaskUp(@RequestParam(value = "name") String name, Model model) {
        if (this.service.getAlbum() != null) {
            this.service.moveTask(name, false);
            return "redirect:/tasks";
        }
        return "redirect:/albums";
    }

    @RequestMapping("/reorder/task/down") //move task later in the order
    public String moveTaskDown(@RequestParam(value = "name") String name, Model model) {
        if (this.service.getAlbum() != null) {
            this.service.moveTask(name, true);
            return "redirect:/tasks";
        }
        return "redirect:/albums";
    }

    @RequestMapping("/task/setphase") //move task later in the order
    public String setTaskPhase(@RequestParam(value = "value") String value, Model model) {
        if (this.service.getAlbum() != null) {
//            System.out.println("set task/phase" + value);
            this.service.setTaskPhase(value);

            return "redirect:/tasks";
        }
        return "redirect:/albums";
    }

    @RequestMapping("/settaskstatus")
    public String setTaskStatus(@RequestParam(value = "songname") String songName, @RequestParam(value = "taskname") String taskName, @RequestParam(value = "value") String value, @RequestParam(value = "returnto") String returnto, Model model) {
        if (this.service.getAlbum() != null) {
            this.service.setTaskStatus(songName, taskName, value);

            String returnString = "redirect:/" + returnto.trim();
            returnString = returnString.replace("//", "/").replace("index", "");
            return returnString;
        }
        return "redirect:/albums";
    }

    @RequestMapping(value = "/subtaskstatus", method = RequestMethod.GET)
    public String setSubtaskStatus(@RequestParam(value = "songname") String songname, @RequestParam(value = "taskname") String taskname, @RequestParam(value = "subtaskname") String subtaskname, @RequestParam(value = "value") String value, @RequestParam(value = "returnto") String returnto, Model model) {
        this.service.setSubtaskStatus(songname, taskname, subtaskname, value);

        Task task = this.service.getAlbum().getSong(songname).getTask(taskname).getTask(subtaskname);
        model.addAttribute("task", task);
        String returnString = "redirect:/" + returnto.trim();
        returnString = returnString.replace("//", "/").replace("index", "");
        return returnString;
    }

    @RequestMapping("/song/addcomment") //move song later in the order
    public String addSongComment(@RequestParam(value = "songname") String songName, @RequestParam(value = "returnto") String returnto, @RequestParam(value = "commenttext") String commentText, Model model) {
        if (this.service.getAlbum() != null) {
            this.service.addSongComment(songName, commentText);
            String returnString = "redirect:/" + returnto.trim();
            returnString = returnString.replace("//", "/").replace("index", "");
            return returnString;
        }
        return "redirect:/albums";
    }

    @RequestMapping("/album/addcomment") //move song later in the order
    public String addAlbumComment(@RequestParam(value = "commenttext") String commentText, Model model) {
        if (this.service.getAlbum() != null) {
            this.service.addAlbumComment(commentText);
            return "redirect:/edit";
        }
        return "redirect:/albums";
    }

    @RequestMapping("/song/setnotes") //move song later in the order
    public String setSongNotes(@RequestParam(value = "songname") String songName, @RequestParam(value = "returnto") String returnto, @RequestParam(value = "notestext") String notesText, Model model) {
        if (this.service.getAlbum() != null) {
            this.service.setSongNotes(songName, notesText);

            String returnString = "redirect:/" + returnto.trim();
            returnString = returnString.replace("//", "/").replace("index", "");
            return returnString;
        }
        return "redirect:/albums";
    }

    @RequestMapping("/album/setnotes") //move song later in the order
    public String setAlbumNotes(@RequestParam(value = "notestext") String notesText, Model model) {
        if (this.service.getAlbum() != null) {
            this.service.setAlbumNotes(notesText);

            return "redirect:/edit";
        }
        return "redirect:/albums";
    }

//    @PutMapping("/songs/{id}") //update name/notes for specified song from current album
//
//    //delete
    @RequestMapping("/delete/album") //set specified album as current album
    public String deleteAlbum(@RequestParam(value = "id") String id, Model model) {
//        System.out.println("deleting");
        if (this.service.getAlbumRepository().count() > 0) {

            Optional<Album> toDelete = this.service.getAlbumRepository().findById(Long.valueOf(id));
            if (toDelete.isPresent()) {
                if (toDelete.get().getOwner().equals(this.service.getUser())) {
                    this.service.getAlbumRepository().deleteById(Long.valueOf(id));
                    return "redirect:/albums";
                } else {
                    model.addAttribute("error", "Only the owner can delete the album.");
                    return "error";
                }
            }
        }
        System.out.println("delete failed: id was " + id);
        return "redirect:/albums";
    }

    @RequestMapping("/delete/song") //set specified album as current album
    public String deleteSong(@RequestParam(value = "song") String song, @RequestParam(value = "returnto") String returnto, Model model) {
        if (this.service.getAlbum() != null) {

            if (this.service.getAlbum().getOwner().equals(this.service.getUser())) {
                this.service.deleteSong(song);
                String returnString = "redirect:/" + returnto.trim();
                returnString = returnString.replace("//", "/").replace("index", "");
                return returnString;
            } else {
                model.addAttribute("error", "Only the owner can delete songs.");
                return "error";
            }
        }
        return "redirect:/albums";
    }

    @RequestMapping("/delete/phase") //set specified album as current album
    public String deletePhase(@RequestParam(value = "phasenumber") String phasenumber, Model model) {
        if (this.service.getAlbum() != null) {
            if (this.service.getAlbum().getOwner().equals(this.service.getUser())) {
                this.service.deletePhase(Integer.valueOf(phasenumber));
                return "redirect:/tasks";
            } else {
                model.addAttribute("error", "Only the owner can delete phases.");
                return "error";
            }
        }
        return "redirect:/albums";
    }

    @RequestMapping("/delete/task") //set specified album as current album
    public String deleteTask(@RequestParam(value = "task") String task, Model model) {
        if (this.service.getAlbum() != null) {
            if (this.service.getAlbum().getOwner().equals(this.service.getUser())) {
                this.service.deleteTask(task);
                return "redirect:/tasks";
            } else {
                model.addAttribute("error", "Only the owner can delete tasks.");
                return "error";
            }
        }

        return "redirect:/albums";
    }

}
