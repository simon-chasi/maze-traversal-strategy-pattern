package com.mazedata;

public record MazeTraversalSolution(
        Maze maze,
        boolean[][] traversedMazeBoard
) {}