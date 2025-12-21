package com.strategies;

import com.mazedata.Maze;
import com.mazedata.MazeField;

import java.util.List;

/**
 * <p>
 *     A class which extends the abstract class {@code GuaranteedMazeTraverser} and
 *     is guaranteed to find a traversal path for a maze if one exists.
 * </p>
 *
 * This abstract class provides a method to convert a list of traversed {@link MazeField}s into a traversed board.
 */
public abstract class GuaranteedMazeTraverser implements MazeTraversalStrategy {
    /**
     * <p>
     *     The method uses {@link #traverseMaze(Maze)} in attempt to traverse a maze
     *     and returns {@code true} on success, {@code false} otherwise.
     * </p>
     * To find out the reason of untraversability call {@link #traverseMaze(Maze)} directly.
     *
     * @param maze The maze to be analyzed
     * @return {@code true} if the maze is traversable, {@code false} if not
     */
    public boolean mazeIsTraversable(Maze maze) {
        try {
            traverseMaze(maze);
            return true;
        } catch (MazeNotTraversableException e) {
            return false;
        }
    }

    /**
     * Converts a list of traversed maze fields into a traversed maze board.
     *
     * @param pathFields A list containing maze fields stored during the maze traversal process
     * @param boardHeight The maze board height
     * @param boardWidth The maze board width
     * @return The traversed maze board
     */
    protected boolean[][] pathFieldsToTraversedBoard(List<MazeField> pathFields, int boardHeight, int boardWidth) {
        boolean[][] traversedMazeBoard = new boolean[boardHeight][boardWidth];

        for (MazeField field : pathFields) {
            traversedMazeBoard[field.positionY()][field.positionX()] = true;
        }
        return traversedMazeBoard;
    }
}
