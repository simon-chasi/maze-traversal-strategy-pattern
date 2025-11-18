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
        boolean[][] mazeBoard = maze.getMazeBoard().clone();
        boolean[][] traversedMazeBoard = mazeBoard.clone();

        int mazeBoardHeight = mazeBoard.length;
        int mazeBoardWidth = mazeBoard[0].length;

        MazeField currentField = maze.getStartingField();
        MazeField endingField = maze.getEndingField();

        int startingFieldReachedAmount = 0;
        MazeField.BorderingFieldSide nextFieldFirstSide = MazeField.BorderingFieldSide.LEFT;

        int test = 0;
        while (currentField != endingField && test < 5) {
            System.out.println("CURRENT: " + currentField.toString());
            System.out.println("SIDE: " + nextFieldFirstSide.toString());
            if (currentField == maze.getStartingField()) {
                startingFieldReachedAmount++;
                if (startingFieldReachedAmount >= MAX_STARTING_FIELD_REACHED_AMOUNT) {
                    LOGGER.fine(String.format(
                            "The starting field has been reached %d times. The following maze is unsolvable: %s",
                            MAX_STARTING_FIELD_REACHED_AMOUNT, maze
                    ));
                    return null;
                }
            }

            MazeField[] borderingFields = currentField.determineBorderingFields(
                    mazeBoardHeight, mazeBoardWidth, direction, nextFieldFirstSide
            );

            System.out.println("bordering: " + Arrays.toString(borderingFields));

            for (int i = 0; i < borderingFields.length; i++) {
                System.out.println("field: " + borderingFields[i]);
                if (borderingFields[i] != null
                        && mazeBoard[borderingFields[i].positionY()][borderingFields[i].positionX()]) {
                    nextFieldFirstSide = MazeField.BorderingFieldSide.values()[
                            (currentField.borderingFieldSide(borderingFields[i]).ordinal() + 1) / 4
                    ];
                    currentField = borderingFields[i];
                    break;
                }
            }
            System.out.println();
            test++;
        }
        System.out.println("solved");
        return new MazeTraversalSolution(maze, traversedMazeBoard);
    }
}
