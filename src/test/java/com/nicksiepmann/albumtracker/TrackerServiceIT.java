/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package com.nicksiepmann.albumtracker;

import com.nicksiepmann.albumtracker.domain.Album;
import com.nicksiepmann.albumtracker.domain.AlbumRepository;
import com.nicksiepmann.albumtracker.domain.GridBuilder;
import com.nicksiepmann.albumtracker.domain.User;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.BDDMockito.given;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.annotation.DirtiesContext;

/**
 *
 * @author Nick.Siepmann
 */
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@DirtiesContext
public class TrackerServiceIT {

    private TrackerService underTest;

    @Mock
    OAuth2User principal;

    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private GridBuilder gridBuilder;
    @Autowired
    private Emailer emailer;

    @BeforeAll
    public void setUpClass() {
        this.underTest = new TrackerService(albumRepository, gridBuilder, emailer);
    }

    @AfterEach
    public void cleanUp() {
        this.underTest.getAlbumRepository().findByName("Album Title").forEach(s -> {
            this.underTest.getAlbumRepository().delete(s);
        });
    }

    @Test
    public void testNewUser() {
        given(principal.getAttribute("name")).willReturn("User Name");
        given(principal.getAttribute("email")).willReturn("email@gmail.com");
        this.underTest.newUser(principal);
        assertEquals("User Name", this.underTest.getUser().getName()); //remember spaces get replaced!
    }

    @Test
    public void testCleanString() {
        String toClean = this.underTest.cleanString("Test this ̪QQQnonsense");
        assertEquals("Test_this_nonsense", toClean);
    }

    @Test
    public void testNewAlbum() {
        given(principal.getAttribute("name")).willReturn("User Name");
        given(principal.getAttribute("email")).willReturn("email@gmail.com");
        this.underTest.newUser(principal);
        this.underTest.newAlbum("Album Title", "Artist Name");
        assertEquals("User Name", this.underTest.getUser().getName()); //remember spaces get replaced!
        assertEquals("Album Title", this.underTest.getAlbum().getName());
        assertEquals("User Name", this.underTest.getAlbum().getOwner().getName());
        List<Album> myAlbums = this.underTest.listMyAlbums();
        assertEquals(1, myAlbums.size());
        assertEquals("Album Title", myAlbums.get(0).getName());
    }

    @Test
    public void testUpdateWithCurrentEditor() {
        given(principal.getAttribute("name")).willReturn("Original Name");
        given(principal.getAttribute("email")).willReturn("email@gmail.com");
        this.underTest.newUser(principal);
        this.underTest.newAlbum("Album Title", "Artist Name");
        assertEquals("Original Name", this.underTest.getUser().getName()); //remember spaces get replaced!    
        given(principal.getAttribute("name")).willReturn("User Name");
        this.underTest.newUser(principal);
        this.underTest.updateWithCurrentEditor();
        assertEquals("User Name", this.underTest.getUser().getName()); //remember spaces get replaced!    

    }

    @Test
    public void testGetAlbumGrid() {
        given(principal.getAttribute("name")).willReturn("Original Name");
        given(principal.getAttribute("email")).willReturn("email@gmail.com");
        this.underTest.newUser(principal);
        this.underTest.newAlbum("Album Title", "Artist Name");
        this.underTest.addPhase("Phase Name");
        this.underTest.addPhase("Second Phase");
        this.underTest.newTask("New Task");
        this.underTest.newTask("Next Task");
        this.underTest.newTask("Third Task");
        this.underTest.addSong("Song Title");
        this.underTest.addSong("Second Song");
        this.underTest.setTaskPhase("New_Task̪QQQ0"); //set to phase 0
        this.underTest.setTaskPhase("Next_Task̪QQQ0"); //set to phase 0
        this.underTest.setTaskPhase("Third_Task̪QQQ1"); //set to phase 1
        this.underTest.setTaskStatus("Song_Title", "New_Task", "add");
        this.underTest.addSubtask("Song_Title", "New_Task", "New Subtask");
        this.underTest.setTaskStatus("Second_Song", "New_Task", "add");

        String[][] grid = this.underTest.getAlbumGrid();
        assertEquals("Song_Title", grid[2][0]);
        assertEquals("Phase Name", grid[0][2]);
        assertEquals("New_Task", grid[1][2]);
        assertEquals("S", grid[2][2]); //split task
        assertEquals("X", grid[3][2]); //incomplete task
    }

    @Test
    public void testGetSongGrid() {
        given(principal.getAttribute("name")).willReturn("Original Name");
        given(principal.getAttribute("email")).willReturn("email@gmail.com");
        this.underTest.newUser(principal);
        this.underTest.newAlbum("Album Title", "Artist Name");
        this.underTest.addPhase("Phase Name");
        this.underTest.addPhase("Second Phase");
        this.underTest.newTask("New Task");
        this.underTest.newTask("Next Task");
        this.underTest.newTask("Third Task");
        this.underTest.addSong("Song Title");
        this.underTest.setTaskPhase("New_Task̪QQQ0"); //set to phase 0
        this.underTest.setTaskPhase("Next_Task̪QQQ0"); //set to phase 0
        this.underTest.setTaskPhase("Third_Task̪QQQ1"); //set to phase 1
        this.underTest.setTaskStatus("Song_Title", "New_Task", "add");
        this.underTest.addSubtask("Song_Title", "New_Task", "New Subtask");
        this.underTest.setTaskStatus("Song_Title", "Next_Task", "add");

        String[][] grid = this.underTest.getSongGrid(("Song_Title"));
        assertEquals("Phase Name", grid[0][0]);
        assertEquals("Second Phase", grid[2][0]);
        assertEquals("Third_Task", grid[2][1]);
        assertEquals("S", grid[0][2]); //split task
        assertEquals("X", grid[1][2]); //incomplete task
    }

    @Test
    public void testGetSubtasks() {
        given(principal.getAttribute("name")).willReturn("Original Name");
        given(principal.getAttribute("email")).willReturn("email@gmail.com");
        this.underTest.newUser(principal);
        this.underTest.newAlbum("Album Title", "Artist Name");
        this.underTest.addPhase("Phase Name");
        this.underTest.newTask("New Task");
        this.underTest.addSong("Song Title");
        this.underTest.setTaskPhase("New_Task̪QQQ0"); //set to phase 0
        this.underTest.setTaskStatus("Song_Title", "New_Task", "add");
        this.underTest.addSubtask("Song_Title", "New_Task", "New Subtask");
        assertEquals("New_Subtask", this.underTest.getSubtasks("PLACEHOLDER̪QQQSong_Title̪QQQNew_Task").get(0).getName());
    }

    @Test
    public void testAlbumExists() {
        given(principal.getAttribute("name")).willReturn("Original Name");
        given(principal.getAttribute("email")).willReturn("email@gmail.com");
        this.underTest.newUser(principal);
        this.underTest.newAlbum("Album Title", "Artist Name");
        assertTrue(this.underTest.albumExists("Album Title", "Artist Name"));
        assertFalse(this.underTest.albumExists("Other Title", "Other Name"));
    }

    @Test
    public void testRenamePhase() {
        given(principal.getAttribute("name")).willReturn("Original Name");
        given(principal.getAttribute("email")).willReturn("email@gmail.com");
        this.underTest.newUser(principal);
        this.underTest.newAlbum("Album Title", "Artist Name");
        this.underTest.addPhase("Phase Name");
        assertEquals("Phase Name", this.underTest.getAlbum().getIndex().getPhases().get(0));
        this.underTest.renamePhase(0, "New Phase Name");
        assertEquals("New Phase Name", this.underTest.getAlbum().getIndex().getPhases().get(0));
    }

    @Test
    public void testRenameSong() {
        given(principal.getAttribute("name")).willReturn("Original Name");
        given(principal.getAttribute("email")).willReturn("email@gmail.com");
        this.underTest.newUser(principal);
        this.underTest.newAlbum("Album Title", "Artist Name");
        this.underTest.addSong("Old Title");
        this.underTest.renameSong(this.underTest.getAlbum().getSongs().get(0).getName(), "New Title");
        assertEquals("New_Title", this.underTest.getAlbum().getSongs().get(0).getName());
    }

    @Test
    public void testRenameAlbum() {
        given(principal.getAttribute("name")).willReturn("Original Name");
        given(principal.getAttribute("email")).willReturn("email@gmail.com");
        this.underTest.newUser(principal);
        this.underTest.newAlbum("Old Title", "Old Artist");
        this.underTest.renameAlbum(this.underTest.getAlbum().getId(), "Album Title", "Artist Name");
        assertEquals("Album Title", this.underTest.getAlbum().getName());
    }

    @Test
    public void testAddPhase() {
        given(principal.getAttribute("name")).willReturn("Original Name");
        given(principal.getAttribute("email")).willReturn("email@gmail.com");
        this.underTest.newUser(principal);
        this.underTest.newAlbum("Album Title", "Artist Name");
        this.underTest.addPhase("Phase Name");
        assertEquals(1, this.underTest.getAlbum().getIndex().getPhases().size());
    }

    @Test
    public void testNewTask() {
        given(principal.getAttribute("name")).willReturn("Original Name");
        given(principal.getAttribute("email")).willReturn("email@gmail.com");
        this.underTest.newUser(principal);
        this.underTest.newAlbum("Album Title", "Artist Name");
        this.underTest.addPhase("Phase Name");
        this.underTest.newTask("New Task");
        assertTrue(this.underTest.getAlbum().getIndex().getTaskIndex().containsKey("New_Task"));
    }

    @Test
    public void testAddSubtask() {
        given(principal.getAttribute("name")).willReturn("Original Name");
        given(principal.getAttribute("email")).willReturn("email@gmail.com");
        this.underTest.newUser(principal);
        this.underTest.newAlbum("Album Title", "Artist Name");
        this.underTest.addPhase("Phase Name");
        this.underTest.newTask("New Task");
        this.underTest.addSong("Song Title");
        this.underTest.setTaskPhase("New_Task̪QQQ0"); //set to phase 0 (the only one)
        this.underTest.setTaskStatus("Song_Title", "New_Task", "add");
        this.underTest.setTaskStatus("Song_Title", "New_Task", "complete");

        this.underTest.addSubtask("Song_Title", "New_Task", "New Subtask");
        assertEquals("New_Subtask", this.underTest.getAlbum().getSong("Song_Title").getTask("New_Task").getTasks().get(0).getName());
        assertTrue(this.underTest.getAlbum().getSong("Song_Title").getTask("New_Task").isSplit());
        assertFalse(this.underTest.getAlbum().getSong("Song_Title").getTask("New_Task").isDone());

        this.underTest.addSubtask("Song_Title", "New_Task", "Another Subtask");
        this.underTest.setSubtaskStatus("Song_Title", "New_Task", "Another_Subtask", "delete");
        assertEquals(1, this.underTest.getAlbum().getSong("Song_Title").getTask("New_Task").getTasks().size());

        this.underTest.setTaskStatus("Song_Title", "New_Task", "complete");
        assertFalse(this.underTest.getAlbum().getSong("Song_Title").getTask("New_Task").isDone());

        this.underTest.setSubtaskStatus("Song_Title", "New_Task", "New_Subtask", "complete");
        assertTrue(this.underTest.getAlbum().getSong("Song_Title").getTask("New_Task").getTask("New_Subtask").isDone());
        assertTrue(this.underTest.getAlbum().getSong("Song_Title").getTask("New_Task").isDone());
    }

    @Test
    public void testAddSong() {
        given(principal.getAttribute("name")).willReturn("Original Name");
        given(principal.getAttribute("email")).willReturn("email@gmail.com");
        this.underTest.newUser(principal);
        this.underTest.newAlbum("Album Title", "Artist Name");
        this.underTest.addSong("Song Title");
        assertEquals("Song_Title", this.underTest.getAlbum().getSongs().get(0).getName());
    }

    @Test
    public void testAddEditor() {
        given(principal.getAttribute("name")).willReturn("Original Name");
        given(principal.getAttribute("email")).willReturn("email@gmail.com");
        this.underTest.newUser(principal);
        this.underTest.newAlbum("Album Title", "Artist Name");
        User newUser = new User("New Editor", "newemail@gmail.com");
        this.underTest.addEditor(newUser);
        assertEquals("New Editor", this.underTest.getAlbum().getEditors().get(1).getName());
    }

    @Test
    public void testRemoveEditor() {
        given(principal.getAttribute("name")).willReturn("Original Name");
        given(principal.getAttribute("email")).willReturn("email@gmail.com");
        this.underTest.newUser(principal);
        this.underTest.newAlbum("Album Title", "Artist Name");
        User newUser = new User("New Editor", "newemail@gmail.com");
        this.underTest.addEditor(newUser);
        assertEquals("New Editor", this.underTest.getAlbum().getEditors().get(1).getName());
        this.underTest.removeEditor(newUser);
        assertEquals(1, this.underTest.getAlbum().getEditors().size());
    }

    @Test
    public void testMoveSong() {
        given(principal.getAttribute("name")).willReturn("Original Name");
        given(principal.getAttribute("email")).willReturn("email@gmail.com");
        this.underTest.newUser(principal);
        this.underTest.newAlbum("Album Title", "Artist Name");
        this.underTest.addSong("Song Title");
        this.underTest.addSong("Song 2");
        assertEquals("Song_Title", this.underTest.getAlbum().getSongs().get(0).getName());
        this.underTest.moveSong("Song_Title", true); //testing normal increase
        assertEquals("Song_2", this.underTest.getAlbum().getSongs().get(0).getName());
        this.underTest.moveSong("Song_Title", true); //testing invalid increase - out of bounds
        assertEquals("Song_Title", this.underTest.getAlbum().getSongs().get(1).getName());
        this.underTest.moveSong("Song_Title", false); //testing valid decrease
        assertEquals("Song_Title", this.underTest.getAlbum().getSongs().get(0).getName());
        this.underTest.moveSong("Song_Title", false); //testing invalid decrease - out of bounds
        assertEquals("Song_Title", this.underTest.getAlbum().getSongs().get(0).getName());
    }

    @Test
    public void testMoveTask() {
        given(principal.getAttribute("name")).willReturn("Original Name");
        given(principal.getAttribute("email")).willReturn("email@gmail.com");
        this.underTest.newUser(principal);
        this.underTest.newAlbum("Album Title", "Artist Name");
        this.underTest.addPhase("Phase Name");
        this.underTest.addPhase("Second Phase");
        this.underTest.newTask("New Task");
        this.underTest.newTask("Next Task");
        this.underTest.newTask("Third Task");
        this.underTest.setTaskPhase("New_Task̪QQQ0"); //set to phase 0
        this.underTest.setTaskPhase("Next_Task̪QQQ0"); //set to phase 0
        this.underTest.setTaskPhase("Third_Task̪QQQ1"); //set to phase 1

        this.underTest.moveTask("New_Task", false); //invalid decrease - out of bounds
        assertEquals((int) 0, (int) this.underTest.getAlbum().getIndex().getTaskIndex().get("New_Task")[0]); //check task order
        assertEquals((int) 0, (int) this.underTest.getAlbum().getIndex().getTaskIndex().get("New_Task")[1]); //check task phase

        this.underTest.moveTask("Third_Task", true); //invalid increase - out of bounds
        assertEquals((int) 2, (int) this.underTest.getAlbum().getIndex().getTaskIndex().get("Third_Task")[0]); //check task order
        assertEquals((int) 1, (int) this.underTest.getAlbum().getIndex().getTaskIndex().get("Third_Task")[1]); //check task phase

        this.underTest.moveTask("New_Task", true); //valid increase
        assertEquals((int) 1, (int) this.underTest.getAlbum().getIndex().getTaskIndex().get("New_Task")[0]); //check task order
        assertEquals((int) 0, (int) this.underTest.getAlbum().getIndex().getTaskIndex().get("New_Task")[1]); //check task phase

        this.underTest.moveTask("New_Task", false); //valid decrease
        assertEquals((int) 0, (int) this.underTest.getAlbum().getIndex().getTaskIndex().get("New_Task")[0]); //check task order
        assertEquals((int) 0, (int) this.underTest.getAlbum().getIndex().getTaskIndex().get("New_Task")[1]); //check task phase

    }

    @Test
    public void testSetTaskPhase() {
        given(principal.getAttribute("name")).willReturn("Original Name");
        given(principal.getAttribute("email")).willReturn("email@gmail.com");
        this.underTest.newUser(principal);
        this.underTest.newAlbum("Album Title", "Artist Name");
        this.underTest.addPhase("Phase Name");
        this.underTest.newTask("New Task");
        this.underTest.newTask("Next Task");
        assertEquals((int) 0, (int) this.underTest.getAlbum().getIndex().getTaskIndex().get("Next_Task")[1]); //check task phase
        this.underTest.addPhase("Second Phase");
        this.underTest.newTask("Third Task");
        assertEquals((int) 0, (int) this.underTest.getAlbum().getIndex().getTaskIndex().get("Third_Task")[1]); //check task phase
        this.underTest.setTaskPhase("New_Task̪QQQ1"); //set to phase 1 (which should affect any after it)
        assertEquals((int) 1, (int) this.underTest.getAlbum().getIndex().getTaskIndex().get("New_Task")[1]); //check task phase 
        assertEquals((int) 1, (int) this.underTest.getAlbum().getIndex().getTaskIndex().get("Next_Task")[1]); //check task phase
        this.underTest.setTaskPhase("Next_Task̪QQQ0"); //set to phase 0 (which should affect any after it)
        assertEquals((int) 0, (int) this.underTest.getAlbum().getIndex().getTaskIndex().get("New_Task")[1]); //check task phase
    }

    @Test
    public void testSetTaskStatus() {
        given(principal.getAttribute("name")).willReturn("Original Name");
        given(principal.getAttribute("email")).willReturn("email@gmail.com");
        this.underTest.newUser(principal);
        this.underTest.newAlbum("Album Title", "Artist Name");
        this.underTest.addPhase("Phase Name");
        this.underTest.newTask("New Task");
        this.underTest.addSong("Song Title");
        this.underTest.setTaskPhase("New_Task̪QQQ0"); //set to phase 0 (the only one)
        assertEquals(0, this.underTest.getAlbum().getSong("Song_Title").getTasks().size());
        assertTrue(this.underTest.getAlbum().getIndex().getTaskIndex().keySet().contains("New_Task"));
        this.underTest.setTaskStatus("Song_Title", "New_Task", "add");
        assertFalse(this.underTest.getAlbum().getSong("Song_Title").getTask("New_Task").isDone());
        assertEquals(1, this.underTest.getAlbum().getSong("Song_Title").getTasks().size());
        this.underTest.setTaskStatus("Song_Title", "New_Task", "complete");
        assertTrue(this.underTest.getAlbum().getSong("Song_Title").getTask("New_Task").isDone());
        this.underTest.setTaskStatus("Song_Title", "New_Task", "incomplete");
        assertFalse(this.underTest.getAlbum().getSong("Song_Title").getTask("New_Task").isDone());
        this.underTest.setTaskStatus("Song_Title", "New_Task", "delete");
        assertEquals(0, this.underTest.getAlbum().getSong("Song_Title").getTasks().size());
    }

    @Test
    public void testAddSongComment() {
        given(principal.getAttribute("name")).willReturn("User Name");
        given(principal.getAttribute("email")).willReturn("email@gmail.com");
        this.underTest.newUser(principal);
        this.underTest.newAlbum("Album Title", "Artist Name");
        this.underTest.addSong("Song Title");
        this.underTest.addSongComment("Song_Title", "This is a comment");
        assertEquals("This is a comment", this.underTest.getAlbum().getSong("Song_Title").getComments().get(0).getCommentText());
        assertEquals("User Name", this.underTest.getAlbum().getSong("Song_Title").getComments().get(0).getUser().getName());
    }

    @Test
    public void testAddAlbumComment() {
        given(principal.getAttribute("name")).willReturn("User Name");
        given(principal.getAttribute("email")).willReturn("email@gmail.com");
        this.underTest.newUser(principal);
        this.underTest.newAlbum("Album Title", "Artist Name");
        this.underTest.addAlbumComment("This is a comment");
        assertEquals("This is a comment", this.underTest.getAlbum().getComments().get(0).getCommentText());
        assertEquals("User Name", this.underTest.getAlbum().getComments().get(0).getUser().getName());
        assertEquals(LocalDate.now().toString(), this.underTest.getAlbum().getComments().get(0).getTimestamp().subSequence(0, 10));
    }

    @Test
    public void testSetSongNotes() {
        given(principal.getAttribute("name")).willReturn("User Name");
        given(principal.getAttribute("email")).willReturn("email@gmail.com");
        this.underTest.newUser(principal);
        this.underTest.newAlbum("Album Title", "Artist Name");
        this.underTest.addSong("Song Title");
        this.underTest.setSongNotes("Song_Title", "This is a note");
        assertEquals("This is a note", this.underTest.getAlbum().getSong("Song_Title").getNotes());
    }

    @Test
    public void testSetAlbumNotes() {
        given(principal.getAttribute("name")).willReturn("User Name");
        given(principal.getAttribute("email")).willReturn("email@gmail.com");
        this.underTest.newUser(principal);
        this.underTest.newAlbum("Album Title", "Artist Name");
        this.underTest.setAlbumNotes("This is a note");
        assertEquals("This is a note", this.underTest.getAlbum().getNotes());
    }

    @Test
    public void testDeleteSong() {
        given(principal.getAttribute("name")).willReturn("Original Name");
        given(principal.getAttribute("email")).willReturn("email@gmail.com");
        this.underTest.newUser(principal);
        this.underTest.newAlbum("Album Title", "Artist Name");
        this.underTest.addPhase("Phase Name");
        this.underTest.newTask("New Task");
        this.underTest.addSong("Song Title");
        assertEquals(1, this.underTest.getAlbum().getSongs().size());
        this.underTest.deleteSong("Song_Title");
        assertEquals(0, this.underTest.getAlbum().getSongs().size());
    }

    @Test
    public void testDeletePhase() {
        given(principal.getAttribute("name")).willReturn("Original Name");
        given(principal.getAttribute("email")).willReturn("email@gmail.com");
        this.underTest.newUser(principal);
        this.underTest.newAlbum("Album Title", "Artist Name");
        this.underTest.addPhase("Phase Name");
        this.underTest.addPhase("Second Phase");
        this.underTest.addPhase("Third Phase");
        this.underTest.newTask("New Task");
        this.underTest.setTaskPhase("New_Task̪QQQ2"); //set to phase 2 

        assertEquals(3, this.underTest.getAlbum().getIndex().getPhases().size());
        this.underTest.deletePhase(1);
        assertEquals(2, this.underTest.getAlbum().getIndex().getPhases().size());
        assertEquals("Third Phase", this.underTest.getAlbum().getIndex().getPhases().get(1));
        assertEquals((int) 1, (int) this.underTest.getAlbum().getIndex().getTaskIndex().get("New_Task")[1]); //check task phase has been adjusted
    }

    @Test
    public void testDeleteTask() {
        given(principal.getAttribute("name")).willReturn("Original Name");
        given(principal.getAttribute("email")).willReturn("email@gmail.com");
        this.underTest.newUser(principal);
        this.underTest.newAlbum("Album Title", "Artist Name");
        this.underTest.addPhase("Phase Name");
        this.underTest.newTask("New Task");
        this.underTest.newTask("Next Task");
        this.underTest.newTask("Third Task");
        this.underTest.deleteTask("Next_Task");
        assertEquals(2, this.underTest.getAlbum().getIndex().getTaskIndex().size());
        assertEquals((int) 1, (int) this.underTest.getAlbum().getIndex().getTaskIndex().get("Third_Task")[0]); //check task order has been adjusted to remove gap

    }
}
