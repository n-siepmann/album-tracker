/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nicksiepmann.albumtracker;

import java.util.ArrayList;

/**
 *
 * @author Nick.Siepmann
 */
public class Phase extends TaskContainer {

    private String name;
    private int priority;


    public Phase() {
    }

    public Phase(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }


    @Override
    public String toString() {
        return "Phase{" + "name=" + name + ", priority=" + priority + ", tasks=" + super.getTasks().toString() + '}';
    }
    

}
