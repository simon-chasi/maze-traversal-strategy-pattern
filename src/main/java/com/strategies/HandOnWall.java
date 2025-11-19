package com.strategies;

import com.mazedata.Maze;
import com.mazedata.MazeField;
import com.mazedata.MazeTraversalSolution;

import java.util.Arrays;
import java.util.logging.Logger;

public class HandOnWall implements MazeTraversalStrategy {
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

    @Override
    public MazeTraversalSolution traverseMaze(Maze maze) {
        boolean[][] mazeBoard = maze.getMazeBoard();
        boolean[][] traversedMazeBoard = new boolean[mazeBoard.length][mazeBoard[0].length];

        MazeField currentField = maze.getStartingField();
        MazeField endingField = maze.getEndingField();

        MazeField.BorderingFieldSide nextFieldFirstSide = determineStartingSide(mazeBoard, currentField);
        System.out.println("First side: " + nextFieldFirstSide);
        int startingFieldReachedAmount = 0;
        int test = 0;

        while (!currentField.equals(endingField) && test < 250) {
            System.out.println("CURRENT: " + currentField);
            System.out.println("SIDE: " + nextFieldFirstSide.toString());
            try {
                startingFieldReachedAmount = increaseStartingFieldReachedAmount(
                        maze, currentField, startingFieldReachedAmount
                );
            } catch (MazeNotTraversableException e) {
                LOGGER.info(e.getMessage());
                return null;
            }

            MazeField[] borderingFields = currentField.determineBorderingFields(
                    mazeBoard.length, mazeBoard[0].length, direction, nextFieldFirstSide
            );

            System.out.println("bordering: " + Arrays.toString(borderingFields));

            for (MazeField field : borderingFields) {
                System.out.println("field: " + field);
                if (field != null && mazeBoard[field.positionY()][field.positionX()]) {
                    nextFieldFirstSide = currentField.borderingFieldSide(field).next(direction.inverse());
                    currentField = field;
                    traversedMazeBoard[field.positionY()][field.positionX()] = true;
                    break;
                }
            }
            System.out.println();
            test++;
        }
        System.out.println("-----------------------\n---------------------\n--------------------\n");
        return new MazeTraversalSolution(maze, traversedMazeBoard);
    }

    /**
     * Checks if at least one of the surrounding fields of the starting field is {@code null} or a wall
     * and returns the side that field is positioned at in comparison with the starting field.
     *
     * @param board The maze board
     * @param startingField The starting field of the maze
     * @return as described above
     */
    private MazeField.BorderingFieldSide determineStartingSide(boolean[][] board, MazeField startingField) {
        // The pick of the first side is arbitrary
        MazeField[] borderingFields = startingField.determineBorderingFields(board.length, board[0].length, direction);
        for (int i = 0; i < borderingFields.length; i++) {
            MazeField field = borderingFields[i];
            if (field == null || !board[field.positionY()][field.positionX()]) {
                // TODO
                return MazeField.BorderingFieldSide.values()[i];//.next(direction);
            }
        }
        // Return TOP as default if no walls are surrounding the starting field
        return MazeField.BorderingFieldSide.TOP;
    }

    /**
     *
     *
     * @param maze
     * @param currentField
     * @param startingFieldReachedAmount
     * @return
     */
    private int increaseStartingFieldReachedAmount(Maze maze, MazeField currentField, int startingFieldReachedAmount) {
        int amount = startingFieldReachedAmount;
        if (currentField.equals(maze.getStartingField())) {
            amount++;
            if (amount >= MAX_STARTING_FIELD_REACHED_AMOUNT) {
                throw new MazeNotTraversableException(
                        side + " hand on wall",
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
}
