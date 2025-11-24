package com.strategies;

import com.mazedata.Maze;
import com.mazedata.MazeField;

import java.util.logging.Logger;

/**
 * <p>
 *     The hand on wall maze traversal strategy works by holding the left or right hand on the wall and trying
 *     to stick to it from the starting to the ending field.
 * </p>
 * The concrete explanation of how the strategy works is given at {@link #traverseMaze(Maze)}.
 */
public class HandOnWall implements MazeTraversalStrategy {
    /**
     * Contains the two possible implementations of the hand on wall maze traversal strategy:
     * left-hand on wall and right-hand on wall.
     */
    public enum HandOnWallSide {
        LEFT, RIGHT
    }

    /**
     * Logger for tracking events and errors in the {@link HandOnWall} class.
     */
    private static final Logger LOGGER = Logger.getLogger(HandOnWall.class.getName());

    /**
     * Specifies the amount of times a starting field can be reached,
     * before the maze is classified as unsolvable using this strategy.
     */
    private static final int MAX_STARTING_FIELD_REACHED_AMOUNT = 5;

    private final HandOnWallSide side;

    /**
     * When this strategy chooses the next field to pass this attribute specifies if, starting from the
     * dynamically selected first bordering field, the next one should be chosen going clockwise or counterclockwise.
     */
    private final MazeField.BorderingFieldsDirection direction;

    public HandOnWall(HandOnWallSide side) {
        if (side == null) {
            throw new IllegalArgumentException(
                    "A side must be provided in order to create a hand-on-wall maze traversal strategy."
            );
        }

        this.side = side;
        this.direction = side == HandOnWallSide.LEFT
                ? MazeField.BorderingFieldsDirection.CLOCKWISE
                : MazeField.BorderingFieldsDirection.COUNTER_CLOCKWISE;
    }

    /**
     * <p>
     *     The hand on wall strategy follows the principle of sticking to one side of the wall. To ensure this
     *     the following method tries to always turn left or right depending on the chosen {@link HandOnWallSide}.
     * </p>
     * Considering the following simple maze and using the right-hand on wall strategy, the maze follower would
     * first determine the position of the field which <u><i>relatively</i></u> (<u><i>not absolutely</i></u>)
     * lies on the right, taking into consideration the previous direction ({@code ←}). The field in question is
     * {@code #} which lies above the starting field ({@code S}) but since the maze follower is moving left the
     * upper field lies relatively on the right side and is selected as the first possible turning option.
     * Being a wall the maze follower searches for the next path field in a {@code counterclockwise} direction
     * and chooses it as the next field.
     * <p>
     *     The process is repeated inside a {@code while}-loop until the ending field is reached and is the same
     *     for the left-hand on wall strategy, the only difference being the usage of a {@code clockwise} direction.
     * </p>
     * <pre>
     * Eo#
     * #o#
     * #oS ←
     * </pre>
     * <p>
     *     <p>
     *         The strategy may fail if the starting field only borders path fields, since in that case
     *         no wall can be followed. As a result, the maze follower would be trapped inside a loop.
     *     </p>
     *     These occurrences are recognized and handled with the intention to avoid endless loops.
     * </p>
     *
     * @param maze {@inheritDoc}
     * @return {@inheritDoc}
     * @throws MazeNotTraversableException {@inheritDoc}
     */
    @Override
    public boolean[][] traverseMaze(Maze maze) {
        boolean[][] mazeBoard = maze.getMazeBoard();

        MazeField currentField = maze.getStartingField();
        MazeField endingField = maze.getEndingField();

        // Initially only filled with one true value at the starting field since no path has been crossed
        boolean[][] traversedMazeBoard = new boolean[mazeBoard.length][mazeBoard[0].length];
        traversedMazeBoard[currentField.positionY()][currentField.positionX()] = true;

        MazeField.BorderingFieldSide nextFieldFirstSide = determineStartingSide(mazeBoard, currentField);
        int startingFieldReachedAmount = 0;

        while (!currentField.equals(endingField)) {
            startingFieldReachedAmount = increaseStartingFieldReachedAmount(
                    maze, currentField, startingFieldReachedAmount
            );
            MazeField nextField = determineNextField(
                    currentField, nextFieldFirstSide, mazeBoard, traversedMazeBoard, maze
            );

            // Determine next side which lies relatively on the right
            nextFieldFirstSide = currentField.borderingFieldSide(nextField).next(direction.inverse());
            currentField = nextField;
        }

        return traversedMazeBoard;
    }

    /**
     * Checks if at least one of the surrounding fields of the starting field is {@code null} or a wall
     * and, starting from this field and moving to the starting field, returns the side which lies on the
     * left (left-hand on wall) or right (right-hand on wall) to it.
     *
     * @param board The maze board
     * @param startingField The starting field of the maze
     * @return as described above
     */
    private MazeField.BorderingFieldSide determineStartingSide(boolean[][] board, MazeField startingField) {
        // Start from TOP since it's always the first in order
        MazeField[] borderingFields = startingField.determineBorderingFields(board.length, board[0].length, direction);
        for (int i = 0; i < borderingFields.length; i++) {
            MazeField field = borderingFields[i];
            if (field == null || !board[field.positionY()][field.positionX()]) {
                // Moving from null / wall choose the next field in order to directly be able to turn right or left
                return MazeField.BorderingFieldSide.order(direction)[i].next(direction);
            }
        }
        // Return TOP as default if no walls are surrounding the starting field
        return MazeField.BorderingFieldSide.TOP;
    }

    /**
     * If the starting field has been reached {@value MAX_STARTING_FIELD_REACHED_AMOUNT} times during
     * the maze traversal process the maze cannot be traversed since the maze follower is situated in
     * a loop. In this case this method throws a {@link MazeNotTraversableException}, otherwise the
     * number of times the starting field has been reached is increased by one.
     *
     * @param maze The maze to be traversed
     * @param currentField A field the maze follower is at in the maze traversal process
     * @param startingFieldReachedAmount Number of times the starting point has been reached so far
     * @return as described above
     * @throws MazeNotTraversableException as described above
     */
    private int increaseStartingFieldReachedAmount(Maze maze, MazeField currentField, int startingFieldReachedAmount) {
        int amount = startingFieldReachedAmount;
        if (currentField.equals(maze.getStartingField())) {
            amount++;
            if (amount >= MAX_STARTING_FIELD_REACHED_AMOUNT) {
                throw new MazeNotTraversableException(
                        side + "-hand on wall",
                        maze,
                        String.format(
                                "The starting field has been reached %d times.",
                                MAX_STARTING_FIELD_REACHED_AMOUNT
                        )
                );
            }
        }
        return amount;
    }

    /**
     * Chooses the next possible path field which borders the current one starting
     * from the given side and going in the specified direction.
     *
     * @param currentField A field the maze follower is at in the maze traversal process
     * @param mazeBoard The initial maze board of the maze
     * @param firstSide The side at which the first bordering field lies
     * @param traversedMazeBoard The current state of the traversed maze board
     * @param maze The maze to be traversed
     *
     * @return as described above
     * @throws MazeNotTraversableException If none of the bordering fields can be selected as the next field
     */
    private MazeField determineNextField(
            MazeField currentField, MazeField.BorderingFieldSide firstSide,
            boolean[][] mazeBoard, boolean[][] traversedMazeBoard, Maze maze
    ) {
        MazeField[] borderingFields = currentField.determineBorderingFields(
                mazeBoard.length, mazeBoard[0].length, direction, firstSide
        );

        for (MazeField field : borderingFields) {
            // if field not inexistent or a wall, add its position to the traversed maze board and return it
            if (field != null && mazeBoard[field.positionY()][field.positionX()]) {
                traversedMazeBoard[field.positionY()][field.positionX()] = true;
                return field;
            }
        }

        throw new MazeNotTraversableException(
                side + "-hand on wall",
                maze,
                String.format(
                        "The current field (%s) is only surrounded by null fields or walls so that moving further "
                                + "is not possible. This error is expected if the current field is the starting "
                                + "field (%s). If not, the strategy doesn't work properly and needs to be inspected.",
                        currentField, maze.getStartingField()
                )
        );
    }

    public static int getMaxStartingFieldReachedAmount() {
        return MAX_STARTING_FIELD_REACHED_AMOUNT;
    }
}