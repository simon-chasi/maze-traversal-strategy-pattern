package com.strategies;

import com.mazedata.Maze;

/**
 * The interface {@code MazeTraversableCheckStrategy} is meant to be implemented by classes
 * which check if a {@link Maze} is traversable using a specific strategy.
 */
public interface MazeTraversableCheckStrategy {
    /**
     * <p>
     *     Returns {@code true} if the maze is traversable, {@code false} if it is not.
     * </p>
     *
     * @param maze The maze to be analyzed
     * @return {@code true} if the maze is traversable, {@code false} if not
     */
    boolean mazeIsTraversable(Maze maze);
}
