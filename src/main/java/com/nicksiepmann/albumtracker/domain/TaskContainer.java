/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nicksiepmann.albumtracker.domain;

import java.util.ArrayList;

/**
 *
 * @author Nick.Siepmann
 */
public abstract class TaskContainer {

    private ArrayList<Task> tasks;

    public TaskContainer() {
        this.tasks = new ArrayList<>();
    }

    public Task getTask(String name) {
        Task task = new Task(name);
        if (this.tasks.contains(task)) {
            return this.tasks.get(this.tasks.indexOf(task));
        }
        return null;
    }

    public void addTask(String name) {
        Task task = new Task(name);
        this.tasks.add(task);
    }

    public void removeTask(String name) {
        Task task = new Task(name);
        if (this.tasks.contains(task)) {
            this.tasks.remove(this.tasks.indexOf(task));
        }
    }

    public boolean allDone(ArrayList<Task> tasks) {
        boolean allDone = true;
        for (Task task : tasks) {
            if (task.isSplit()) {
                boolean subTasksDone = allDone(task.getTasks());
                if (subTasksDone = true) {
                    task.setDone(true);
                }
            }
            if (!task.isDone()) {
                allDone = false;
            }
        }
        return allDone;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }
//
//    public String cleanString(String input) {
//        String output = input.replace("~", "").replace("¬", "").replace(" ", "_");
//        return output;
//    }

    @Override
    public String toString() {
        return "TaskContainer{" + "tasks=" + tasks + '}';
//    return tasks.toString();
    }

}
