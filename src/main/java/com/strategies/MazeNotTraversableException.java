package com.strategies;

import com.mazedata.Maze;

public class MazeNotTraversableException extends RuntimeException {
    public MazeNotTraversableException(String strategyName, Maze maze, String reason) {
        super(String.join(
                System.lineSeparator(),
                String.format(
                        "Using the strategy \"%s\" the following maze cannot be traversed:", strategyName
                ),
                maze.toString(),
                "Reason: " + reason,
                ""
        ));
    }
}
