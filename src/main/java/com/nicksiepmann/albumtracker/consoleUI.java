/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nicksiepmann.albumtracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author Nick.Siepmann
 */
public class consoleUI {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to album tracker");
//        System.out.println("Album name?");
//        String albumName = scanner.nextLine();
        Album album = new Album("ALBUM");
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
        album.addTaskToSong(song, "NEWPHASETASK");
        album.addTaskToSong(album.getSong("MORESONG"), "NEWPHASETASK");
        song.getTasks().get(1).addTask("SUBTASK");

        GridBuilder gridBuilder = new GridBuilder();

        String[][] grid = gridBuilder.buildGrid(album, true);

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] != null) {
                    System.out.print(grid[i][j]);
                }
                System.out.print("\t");
            }
            System.out.println();
        }

        String msg = scanner.nextLine();

        System.out.println(album.toString());
        song.getTasks().stream().sorted((p1, p2) -> {
            return album.getIndex().getTaskIndex().get(p1.getName())[0] - album.getIndex().getTaskIndex().get(p2.getName())[0];
        }).forEach(s -> System.out.println(s.getName() + "(" + album.getIndex().getPhases().get(album.getIndex().getTaskIndex().get(s.getName())[1]) + ")"));
        album.getIndex().moveTask("TASK", true);
        System.out.println("moved");
        album.getIndex().getTaskIndex().put("NEWPHASETASK", new Integer[]{4, 1});
        System.out.println(album.getIndex().getTaskIndex().get("NEWPHASETASK")[0]);
        album.getIndex().moveTask("TASK", true);
        System.out.println("moved?");
        System.out.println(album.toString());
        song.getTasks().stream().sorted((p1, p2) -> {
            return album.getIndex().getTaskIndex().get(p1.getName())[0] - album.getIndex().getTaskIndex().get(p2.getName())[0];
        }).forEach(s -> System.out.println(s.getName() + "(" + album.getIndex().getPhases().get(album.getIndex().getTaskIndex().get(s.getName())[1]) + ")"));

        System.out.println("commands: add notes, show album, add song, edit song, exit");

        while (scanner.hasNext()) {
            String msg = scanner.nextLine();

            switch (msg) {
                case "exit":
                    return;
                case "add notes":
                    System.out.println("notes:");
                    album.setNotes(scanner.nextLine());
                    break;
                case "show album":
                    break;
                case "add song":
                    System.out.println("name?");
                    String songName = scanner.nextLine();
                    album.addSong(songName);
                    break;
                case "edit song":
                    System.out.println("which song?");
                    songName = scanner.nextLine();
                    song = album.getSong(songName);
                    System.out.println("editing " + song.getName());
                    System.out.println("commands: add notes, add task, add subtask, complete task, exit");
                    loop:
                    while (scanner.hasNext()) {
                        msg = scanner.nextLine();

                        switch (msg) {
                            case "add notes":
                                System.out.println("notes:");
                                song.setNotes(scanner.nextLine());
                                break;
                            case "add task":
                                System.out.println("task name?");
                                String taskName = scanner.nextLine();
                                song.addTask(taskName);
                                break;
                            case "add subtask":
                                System.out.println("add to which task?");
                                taskName = scanner.nextLine();
                                Task task = song.getTask(taskName);
                                System.out.println("subtask name?");
                                String subtaskName = scanner.nextLine();
                                task.addTask(subtaskName);
                                break;
                            case "complete task":
                                System.out.println("task name (if subtask, use format 'task/subtask'?");
                                taskName = scanner.nextLine();
                                if (taskName.contains("/")) {
                                    task = song.getTask(taskName.split("/")[0]).getTask(taskName.split("/")[1]);
                                } else {
                                    task = song.getTask(taskName);
                                }
                                task.setDone(true);
                                break;
                            case "exit":
                                break loop;
                        }
                        System.out.println(album.toString());
                        System.out.println("commands: add task, add subtask, complete task, exit");
                    }

            }
            System.out.println(album.toString());
            System.out.println("commands: add notes, show album, add song, edit song, exit");
        }
//        Album album = new Album("testAlbum");
//        album.addSong(new Song("newSong"));
//        Song song = album.getSong("newSong");
//
//        song.addTask("First task");
//        System.out.println(album.toString());
//        System.out.println("1 all done? " + song.allDone(song.getTasks()));
//
//        song.addTask("Second task");
//        System.out.println(album.toString());
//        System.out.println("2 all done? " + song.allDone(song.getTasks()));
//
//        song.getTask("Second task").addTask("2_subtask");
//        System.out.println(album.toString());
//        System.out.println("3 all done? " + song.allDone(song.getTasks()));
//
//        
//        song.getTask("Second task").setDone(true);
//        System.out.println(album.toString());
//        System.out.println("4 all done? " + song.allDone(song.getTasks()));
//        
//        song.getTask("Second task").getTask("2_subtask").setDone(true);
//        System.out.println(album.toString());
//        System.out.println("5 all done? " + song.allDone(song.getTasks()));
//        System.out.println("5 task all done? " + song.getTask("Second task").allDone(song.getTask("Second task").getTasks()));  
//        
//        song.getTask("First task").setDone(true);
//        System.out.println(album.toString());
//        System.out.println("6 all done? " + song.allDone(song.getTasks()));
//                
//        song.getTask("Second task").setDone(true);
//        System.out.println(album.toString());
//        System.out.println("7 all done? " + song.allDone(song.getTasks()));
    }
}
