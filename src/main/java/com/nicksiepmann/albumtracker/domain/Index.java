/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nicksiepmann.albumtracker.domain;

import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import java.util.ArrayList;
import java.util.HashMap;
import lombok.Data;

/**
 *
 * @author Nick.Siepmann
 */
@Entity
@Data
public class Index {

    private HashMap<String, Integer[]> taskIndex; //task name, [global order, phase number]
    private ArrayList<String> phases;

    public Index() {
        this.taskIndex = new HashMap<>();
        this.phases = new ArrayList<>();
    }

    public Index(HashMap<String, Integer[]> taskIndex, ArrayList<String> phases) {
        this.taskIndex = taskIndex;
        this.phases = phases;
    }

    public void createTask(String name) {
        if (this.phases.isEmpty()) {
            System.out.println("no phases set up");
            return;
        }
        if (!this.taskIndex.containsKey(name)) {
            int latestPhase = taskIndex.values().stream().mapToInt(s -> s[1]).max().orElse(this.phases.size() - 1);
            Integer[] values = {taskIndex.keySet().size(), latestPhase};
            this.taskIndex.put(name, values);
        } else {
            System.out.println("Task already exists; did you want to reorder?");
        }
    }

    public void checkGaps() { // resets the task positions in case anything's gone wrong and there's a discontinuity in the numbering
        Object[] sortedKeySet = taskIndex.keySet().stream().sorted((p1, p2) -> {
            return taskIndex.get(p1)[0] - taskIndex.get(p2)[0];
        }).toArray();

        for (int i = 0; i < sortedKeySet.length; i++) {
            Integer[] val = {i, taskIndex.get((String) sortedKeySet[i])[1]};

            taskIndex.put((String) sortedKeySet[i], val);
        }
    }

    public void moveTask(String name, boolean increase) {

        checkGaps();

        if (increase) {
            Integer[] oldVal = taskIndex.get(name);
            String next = taskIndex.keySet().stream().filter(s -> taskIndex.get(s)[0] == oldVal[0] + 1).findFirst().orElse("");
            Integer[] newVal = taskIndex.get(next);
            if (!next.isEmpty()) {
                taskIndex.put(next, oldVal);
                taskIndex.put(name, newVal);
            }

        } else {
            Integer[] oldVal = taskIndex.get(name);
            String prev = taskIndex.keySet().stream().filter(s -> taskIndex.get(s)[0] == oldVal[0] - 1).findFirst().orElse("");
            Integer[] newVal = taskIndex.get(prev);
            if (!prev.isEmpty()) {
                taskIndex.put(prev, oldVal);
                taskIndex.put(name, newVal);
            }
        }

    }

    public void setTaskPhase(String taskName, int phase) {
        if (taskIndex.containsKey(taskName)) {

            Integer[] val = {taskIndex.get(taskName)[0], phase};
            taskIndex.put(taskName, val);

            // set phase of any tasks earler in the hierarchy set to a higher phase to be equal to the given phase
            taskIndex.values().stream().filter(s -> s[0] < val[0]).filter(s -> s[1] > val[1]).forEach(s -> s[1] = val[1]);
            // set phase of any tasks later in the hierarchy set to a lower phase to avoid doubling back into a previous phase
            taskIndex.values().stream().filter(s -> s[0] > val[0]).filter(s -> s[1] < val[1]).forEach(s -> s[1] = val[1]);
        }

    }

    public String[][] getTasksAndPhases() {
        if (!this.phases.isEmpty()) {
            Object[] sortedKeySet = taskIndex.keySet().stream().sorted((p1, p2) -> {
                return taskIndex.get(p1)[0] - taskIndex.get(p2)[0];
            }).toArray();

            String[][] tasksAndPhases = new String[this.taskIndex.keySet().size()][2];

            for (int i = 0; i < sortedKeySet.length; i++) {
                tasksAndPhases[i][0] = String.valueOf(sortedKeySet[i]);
                tasksAndPhases[i][1] = String.valueOf(taskIndex.get((String) sortedKeySet[i])[1]);

            }

            return tasksAndPhases;
        }
        return null;
    }


    public void addPhase(String name) {
        this.phases.add(name);
    }

    public void renamePhase(String name, int i) {
        if (!this.phases.contains(name)) {
            this.phases.set(i, name);
        }
    }

    public void deletePhase(int phasenumber) {
        this.phases.remove(phasenumber);
        this.taskIndex.values().stream().filter(s -> s[1] >= phasenumber && s[1] > 0).forEach(s -> s[1]--);
    }

    public void deleteTask(String task) {
        this.taskIndex.remove(task);
        checkGaps();
    }
}
