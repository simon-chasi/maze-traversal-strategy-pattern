package com.strategies;

import com.mazedata.Maze;
import com.mazedata.MazeField;

import java.util.*;

/**
 * The breadth first search (BFS) maze traversal strategy works by exploring all neighbours
 * at the current depth before moving to nodes at the next depth level. This guarantees
 * finding the shortest path if one exists.
 */
public class BreadthFirstSearch implements MazeTraversableCheckStrategy, MazeTraversalStrategy {

    /**
     * <p>
     *     The method uses {@link #traverseMaze(Maze)} in attempt to traverse a maze
     *     and returns {@code true} on success, {@code false} otherwise.
     * </p>
     * To find out the reason of untraversability call {@link #traverseMaze(Maze)} directly.
     *
     * @param maze {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean mazeIsTraversable(Maze maze) {
        try {
            traverseMaze(maze);
            return true;
        } catch (MazeNotTraversableException e) {
            return false;
        }
    }

    /**
     * <p>
     *     This method implements the BFS maze traversal strategy by first exploring all neighbouring
     *     fields at a given level before visiting fields of a lower depth.
     * </p>
     * The process continues until the ending field is reached, or the strategy determines
     * that the maze is not traversable.
     * <p>
     *     The strategy <u><i>is guaranteed</i></u> to find the shortest path if one exists.
     * </p>
     *
     * @param maze {@inheritDoc}
     * @return {@inheritDoc}
     * @throws MazeNotTraversableException {@inheritDoc}
     */
    @Override
    public boolean[][] traverseMaze(Maze maze) throws MazeNotTraversableException {
        boolean[][] mazeBoard = maze.getMazeBoard();

        MazeField startingField = maze.getStartingField();
        MazeField endingField = maze.getEndingField();

        // Queue for BFS - processes fields in FIFO order
        Queue<MazeField> fieldsToExplore = new ArrayDeque<>(Collections.singleton(startingField));
        Set<MazeField> encounteredFields = new HashSet<>(Collections.singleton(startingField));
        Map<MazeField, MazeField> predecessorMap = new HashMap<>(); // Key = destination, Value = origin
        predecessorMap.put(startingField, null);

        while (!fieldsToExplore.isEmpty()) {
            MazeField currentField = fieldsToExplore.poll();

            if (currentField.equals(endingField)) {
                List<MazeField> shortestPath = reconstructPathOfSolution(predecessorMap, endingField);
                return pathFieldsToTraversedBoard(shortestPath, mazeBoard.length, mazeBoard[0].length);
            }

            exploreNeighbouringFields(currentField, fieldsToExplore, encounteredFields, predecessorMap, maze);
        }

        throw new MazeNotTraversableException(
                "breadth first search",
                maze,
                String.format(
                        "Starting from %s, all reachable fields have been explored "
                                + "and none leads to the ending field (%s).",
                        maze.getStartingField(), maze.getEndingField()
                )
        );
    }

    /**
     * Explores all neighbouring fields of the current field, adding previously unencountered fields
     * to the queue of fields to explore.
     *
     * @param currentField The field currently being explored
     * @param fieldsToExplore Queue of fields to be explored next
     * @param encounteredFields Set of already encountered fields
     * @param predecessorMap Map tracking the predecessor of each field for path reconstruction
     * @param maze The maze being traversed
     */
    private void exploreNeighbouringFields(
            MazeField currentField, Queue<MazeField> fieldsToExplore, Set<MazeField> encounteredFields,
            Map<MazeField, MazeField> predecessorMap, Maze maze
    ) {
        boolean[][] mazeBoard = maze.getMazeBoard();
        MazeField[] neighbours = currentField.determineBorderingFields(maze.getMazeBoardHeight(),
                maze.getMazeBoardWidth()
        );

        for (MazeField neighbour : neighbours) {
            if (neighbour != null
                    && mazeBoard[neighbour.positionY()][neighbour.positionX()]
                    && !encounteredFields.contains(neighbour)) {
                fieldsToExplore.offer(neighbour);
                encounteredFields.add(neighbour);
                predecessorMap.put(neighbour, currentField);
            }
        }
    }

    /**
     * Reconstructs the shortest path from the starting field to the ending field
     * by tracing back through the predecessor map.
     *
     * @param predecessorMap Map containing the predecessor of each field
     * @param endingField The ending field
     * @return A list of fields representing the shortest path from start to end
     */
    private List<MazeField> reconstructPathOfSolution(Map<MazeField, MazeField> predecessorMap, MazeField endingField) {
        List<MazeField> path = new ArrayList<>();
        MazeField current = endingField;

        while (current != null) {
            path.add(current);
            current = predecessorMap.get(current);
        }

        Collections.reverse(path);
        return path;
    }

    /**
     * Converts a list of traversed maze fields into a traversed maze board.
     *
     * @param pathFields A list containing maze fields in the shortest path
     * @param boardHeight The maze board height
     * @param boardWidth The maze board width
     * @return The traversed maze board
     */
    private boolean[][] pathFieldsToTraversedBoard(List<MazeField> pathFields, int boardHeight, int boardWidth) {
        boolean[][] traversedMazeBoard = new boolean[boardHeight][boardWidth];

        for (MazeField field : pathFields) {
            traversedMazeBoard[field.positionY()][field.positionX()] = true;
        }
        return traversedMazeBoard;
    }
}