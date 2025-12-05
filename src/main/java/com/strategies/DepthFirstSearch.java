package com.strategies;

import com.mazedata.Maze;
import com.mazedata.MazeField;

import java.util.*;

/**
 * <p>
 *     The depth first search (DFS) maze traversal strategy works by exploring one path for as long
 *     as possible until either the ending field or a dead end has been reached. In case of a dead
 *     end the maze follower goes back by one step and continues using an alternative path.
 * </p>
 *
 * This maze traversal strategy is guaranteed to find a traversal path if one exists.
 *
 * @see GuaranteedMazeTraverser
 */
public class DepthFirstSearch extends GuaranteedMazeTraverser {
    /**
     * <p>
     *     This method implements the DFS maze traversal strategy by always trying to move up and if that
     *     is not possible the next not yet passed field is selected in a clockwise direction.
     *     <br>
     *     If a dead end is reached the maze follower backtracks to the last field which borders
     *     a not yet explored path and moves along it.
     * </p>
     * The process is repeated until the ending field is reached,
     * or strategy determines that the maze is not traversable.
     * <p>
     *     The strategy <u><i>is guaranteed</i></u> to find a path which traverses the maze if one exists,
     *     meaning that the strategy can generally be used to determine if a maze is traversable.
     *     <p>
     *         However, it <u><i>is not guaranteed</i></u> to find the shortest path.
     *     </p>
     * </p>
     *
     * @param maze {@inheritDoc}
     * @return {@inheritDoc}
     * @throws MazeNotTraversableException {@inheritDoc}
     */
    @Override
    public boolean[][] traverseMaze(Maze maze) throws MazeNotTraversableException {
        boolean[][] mazeBoard = maze.getMazeBoard();

        MazeField currentField = maze.getStartingField();
        MazeField endingField = maze.getEndingField();

        Set<MazeField> passedFields = new HashSet<>(Collections.singleton(currentField));
        List<MazeField> finalPathFields = new ArrayList<>(Collections.singleton(currentField));

        while (!currentField.equals(endingField)) {
            MazeField nextField = determineNextField(mazeBoard, currentField, passedFields);
            if (nextField != null) {
                passedFields.add(nextField);
                finalPathFields.add(nextField);
                currentField = nextField;
                continue;
            }
            // If a dead end has been reached, move to the previous field and repeat the process
            currentField = determinePreviousField(finalPathFields, maze);
        }

        return pathFieldsToTraversedBoard(finalPathFields, mazeBoard.length, mazeBoard[0].length);
    }

    /**
     * Determines the next field to be traversed by selecting the bordering field which has not yet been traversed
     * starting from the top and going in a clockwise direction.
     *
     * @param mazeBoard The maze board of the maze to be traversed
     * @param currentField The field a maze follower is currently at
     * @param passedFields A list containing the already passed fields
     * @return The next field to be traversed or {@code null} if a dead end has been reached
     */
    private MazeField determineNextField(
            boolean[][] mazeBoard, MazeField currentField, Set<MazeField> passedFields
    ) {
        // Starting side and direction is arbitrary
        MazeField[] borderingFields = currentField.determineBorderingFields(mazeBoard.length, mazeBoard[0].length);
        for (MazeField field : borderingFields) {
            // Choose next not yet passed field
            if (field != null  && mazeBoard[field.positionY()][field.positionX()]
                    && !passedFields.contains(field)) {
                return field;
            }
        }
        return null;
    }

    /**
     * Tries to go back by one field and returns it in order to continue the maze traversal process from
     * that field on. This method is to be used after no further advancement in the maze board is possible.
     *
     * @param pathFields A list containing the fields crossed to reach the exit so far
     * @param maze The maze to be traversed
     * @return as described above
     * @throws MazeNotTraversableException If {@code traversalFields.size() == 1} meaning
     *                                     that the list only contains the starting field.
     */
    private MazeField determinePreviousField(List<MazeField> pathFields, Maze maze) {
        pathFields.removeLast();
        if (pathFields.isEmpty()) {
            throw new MazeNotTraversableException(
                    "depth first search",
                    maze,
                    String.format(
                            "Starting from %s, all possible options have been analyzed "
                                    + "and none leads to the ending field (%s).",
                            maze.getStartingField(), maze.getEndingField()
                    )
            );
        }
        return pathFields.getLast();
    }
}
