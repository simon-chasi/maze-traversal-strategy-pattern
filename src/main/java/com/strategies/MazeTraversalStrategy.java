package com.strategies;

import com.mazedata.Maze;
import com.mazedata.MazeTraversalSolution;

/**
 * The interface {@code MazeTraversalStrategy} is meant to be implemented by classes
 * which traverse a {@link Maze} using a specific strategy.
 */
public interface MazeTraversalStrategy {
    MazeTraversalSolution traverseMaze(Maze maze);
}
