/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nicksiepmann.albumtracker;

import org.springframework.stereotype.Component;

/**
 *
 * @author Nick.Siepmann
 */
@Component
public class GridBuilder {

    public GridBuilder() {
    }

    public String[][] buildGrid(Album album, boolean transposed, String songname) {
        String[][] grid = buildGrid(album);
        String[][] transpGrid = new String[grid[0].length - 2][3];
        for (int i = 1; i < transpGrid.length + 1; i++) {
            transpGrid[i - 1][0] = grid[0][i];
            transpGrid[i - 1][1] = grid[1][i];
            for (int j = 2; j < grid.length; j++) {
                if (grid[j][0].equals(songname)) {
                    transpGrid[i - 1][2] = grid[j][i];
                }
            }
        }

//        System.out.println(transpGrid.length);
        return transpGrid;
    }

    public String[][] buildGrid(Album album) {
        String[][] grid = new String[album.getSongs().size() + 2][album.getIndex().getTaskIndex().keySet().size() + 3]; //extra columns for More Info and Move Song buttons

        for (int i = 0; i < album.getSongs().size(); i++) {
            grid[i + 2][0] = album.getSongs().get(i).getName();
        }

        Object[] sortedKeySet = album.getIndex().getTaskIndex().keySet().stream().sorted((p1, p2) -> {
            return album.getIndex().getTaskIndex().get(p1)[0] - album.getIndex().getTaskIndex().get(p2)[0];
        }).toArray();

        for (int i = 0; i < album.getIndex().getTaskIndex().keySet().size(); i++) {
            grid[0][i + 2] = album.getIndex().getPhases().get(album.getIndex().getTaskIndex().get((String) sortedKeySet[i])[1]);
            grid[1][i + 2] = (String) sortedKeySet[i];
        }

        for (int i = 3; i < grid.length; i++) {
            for (int j = 1; j < grid[0].length - 1; j++) {
                Task task = album.getSong(grid[i][0]).getTask(grid[1][j]);
                if (task != null) {
                    grid[i][j] = "X";
                    if (task.isSplit()) {
                        grid[i][j] = "S";
                    }
                    if (task.isDone()) {
                        grid[i][j] = "Y";
                    }

                }

            }
        }
        return grid;
    }
}
