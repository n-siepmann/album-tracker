/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nicksiepmann.albumtracker.domain;

import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import java.util.ArrayList;
import java.util.Objects;
import lombok.Data;

/**
 *
 * @author Nick.Siepmann
 */
@Data
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
        if (!super.getTasks().contains(new Task(name))) {
            this.split = true;
            this.done = false;
            super.addTask(name);
        }
    }

    public void setDone(boolean done) {
        if (done) {
            if (super.allDone(super.getTasks())) {
                this.done = true;
            }
        } else {
            this.done = false;
        }
    }

    @Override
    public void removeTask(String name) {
        super.removeTask(name);
        if (super.getTasks().isEmpty()) {
            this.split = false;
        }
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

}
