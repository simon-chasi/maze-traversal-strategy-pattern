package com.strategies;

import com.mazedata.Maze;

/**
 * The interface {@code MazeTraversalStrategy} is meant to be implemented by classes
 * which traverse a {@link Maze} using a specific strategy.
 */
public interface MazeTraversalStrategy {
    /**
     * Traverses a maze using a specific strategy.
     *
     * @param maze The maze to be traversed
     * @return A 2d boolean array with {@code true} values representing the crossed path. If the maze is traversable,
     *         the path will lead from the starting to the ending point, if not, the return value will contain the
     *         crossed fields which lead to the conclusion that the maze is untraversable.
     */
    boolean[][] traverseMaze(Maze maze);
}
