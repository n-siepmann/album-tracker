/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nicksiepmann.albumtracker;

import java.util.HashMap;

/**
 *
 * @author Nick.Siepmann
 */
public class TaskIndex {

    private HashMap<String, TaskLocator> taskMap; // key: name; value[phase, position in phase]
    private HashMap<String, Integer[]> phaseMap; //key: name; value: array[position in phase order, number of tasks]

    public TaskIndex() {
        this.taskMap = new HashMap<>();
        this.phaseMap = new HashMap<>();
    }

    public HashMap<String, TaskLocator> getTaskMap() {
        return taskMap;
    }

    public HashMap<String, Integer[]> getPhaseMap() {
        return phaseMap;
    }

    public void addPhase(String name) {
        int position = phaseMap.keySet().size() + 1;
        int taskCount = 0;
        Integer[] values = {position, taskCount};
        phaseMap.put(name, values);
    }

    public void addTask(String name, String phase) {
        if (!this.taskMap.containsKey(name)) {
            this.taskMap.put(name, new TaskLocator(phase, phaseMap.get(phase)[1] + 1));
            phaseMap.get(phase)[1]++;
        } else {
            System.out.println("Task already exists; did you want to reorder?");
        }
    }

    @Override
    public String toString() {
        return "TaskIndex{" + "taskMap=" + taskMap.keySet() + ", phaseMap=" + phaseMap.keySet() + '}';
    }

}
