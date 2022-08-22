/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nicksiepmann.albumtracker;

import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author Nick.Siepmann
 */

public class Task extends TaskContainer {

    private String name;
    private boolean done;
    private boolean split;

    public Task() {
        this.done = false;
        this.split = false;

    }

    public Task(String name) {
        this.name = name;
        this.done = false;
        this.split = false;
    }

    @Override
    public void addTask(String name) {
        this.split = true;
        super.addTask(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        if (super.allDone(super.getTasks())) {
            this.done = done;
        }
    }

    public boolean isSplit() {
        return split;
    }

    public void setSplit(boolean split) {
        this.split = split;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Task other = (Task) obj;
        return Objects.equals(this.name, other.name);
    }

    @Override
    public String toString() {
        return "Task{" + "name=" + name + ", done=" + done + ", split=" + split + ", subtasks=" + super.getTasks().toString() + '}';
    }

}
