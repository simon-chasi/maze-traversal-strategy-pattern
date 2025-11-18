package com.mazedata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The class {@code MazeField} represents a field inside a {@link Maze} board
 * and is characterized by a horizontal and vertical position coordinates.
 *
 * @param positionX Horizontal position
 * @param positionY Vertical position
 */
public record MazeField(
        int positionX,
        int positionY
) {
    public enum BorderingFieldsDirection {
        CLOCKWISE, COUNTER_CLOCKWISE
    }

    public enum BorderingFieldSide {
        TOP, RIGHT, BOTTOM, LEFT
    }

    public MazeField {
        if (positionX < 0 || positionY < 0) {
            throw new  IllegalArgumentException("MazeField position coordinates cannot be negative.");
        }
    }

    /**
     * Returns the four {@link MazeField}s which border this maze field as an array.
     * Starting from the top bordering maze field the direction is clockwise.
     *
     * @param boardHeight The height of the board this field is part of
     * @param boardWidth The width of the board this field is part of
     * @param firstSide Top, right, bottom or left bordering field as the first array element
     * @return as described above
     *
     * @see #determineBorderingFields(int, int, BorderingFieldsDirection, BorderingFieldSide)
     */
    public MazeField[] determineBorderingFields(int boardHeight, int boardWidth, BorderingFieldSide firstSide) {
        return determineBorderingFields(boardHeight, boardWidth, BorderingFieldsDirection.CLOCKWISE, firstSide);
    }

    /**
     * <p>
     *     Returns the four {@link MazeField}s which border this maze field as an array starting from the specified
     *     first side and going either clockwise or counterclockwise.
     * </p>
     *
     * If the maze field is positioned at an edge of the given board, a {@code null} value is stored
     * at the respective index.
     *
     * @param boardHeight The height of the board this field is part of
     * @param boardWidth The width of the board this field is part of
     * @param direction Clockwise or counterclockwise starting from the given side
     * @param firstSide Top, right, bottom or left bordering field as the first array element.
     *                  Defaults to top if {@code null}
     * @return as described above
     */
    public MazeField[] determineBorderingFields(
            int boardHeight, int boardWidth, BorderingFieldsDirection direction, BorderingFieldSide firstSide
    ) {
        MazeField[] borderingMazeFields = new MazeField[4];

        // calculateSequenceInBoard returns the sequence starting from 0,
        // but here a count from 1 is needed because of simpler modulo operations
        int mazeFieldSequence = calculateSequenceInBoard(boardWidth) + 1;

        int[] indexes = determineBorderingFieldsIndexes(
                firstSide == null ? BorderingFieldSide.TOP : firstSide
        );
        System.out.println(Arrays.toString(indexes));
        /*System.out.println("MazeFieldSequence: " + mazeFieldSequence);
        System.out.println("Board width: " + boardWidth);*/

        // Maze field is not on the top edge
        if (mazeFieldSequence > boardWidth) {
            borderingMazeFields[indexes[0]] = new MazeField(positionX, positionY - 1);
        }
        // Maze field is not on the right edge
        if (mazeFieldSequence % boardWidth != 0) {
            borderingMazeFields[indexes[direction == BorderingFieldsDirection.CLOCKWISE ? 1 : 3]]
                    = new MazeField(positionX + 1, positionY);
        }
        // Maze field is not on the bottom edge
        if (mazeFieldSequence < (boardHeight - 1) * boardWidth) {
            borderingMazeFields[indexes[2]] = new MazeField(positionX, positionY + 1);
        }
        // Maze field is not on the left edge
        if (mazeFieldSequence % boardWidth != 1) {
            borderingMazeFields[indexes[direction == BorderingFieldsDirection.CLOCKWISE ? 3 : 1]]
                    = new MazeField(positionX - 1, positionY);
        }
/*
        System.out.println(borderingMazeFields[0]);
        System.out.println(borderingMazeFields[1]);
        System.out.println(borderingMazeFields[2]);
        System.out.println(borderingMazeFields[3]);*/

        return borderingMazeFields;
    }

    /**
     * Returns the sequence of this field in a board of the given width starting with 0.
     *
     * @param boardWidth The width of the board this field is part of
     * @return as described above
     */
    public int calculateSequenceInBoard(int boardWidth) {
        return positionX + boardWidth * positionY;
    }

    /**
     * <p>
     *     Returns four indexes for the bordering fields on the top, right, bottom and left side
     *     of a {@link MazeField} as an integer array.
     *     <br>
     *     This method is used in combination with {@link #determineBorderingFields(int, int,
     *     BorderingFieldsDirection, BorderingFieldSide)} to specify the order of each of the
     *     four bordering maze fields.
     * </p>
     * The array's indexes are mapped to sides as following:
     * <ul>
     *     <li>0 -- Top</li>
     *     <li>1 -- Right</li>
     *     <li>2 -- Bottom</li>
     *     <li>3 -- Left</li>
     * </ul>
     *
     * @param firstSide Top, right, bottom or left as the first array element
     * @return as described above
     */
    private static int[] determineBorderingFieldsIndexes(BorderingFieldSide firstSide) {
        List<Integer> indexes = new ArrayList<>(
                List.of(0, 1, 2, 3) // top, right, bottom, left
        );
        Collections.rotate(indexes, firstSide.ordinal());

        return indexes.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Returns the {@link BorderingFieldSide} on which the passed maze field
     * is positioned in comparison with this object.
     *
     * @param borderingField The object to compare this maze field object with
     * @return as described above
     * @throws IllegalArgumentException if the two maze fields don't border each other
     */
    public BorderingFieldSide borderingFieldSide(MazeField borderingField) {
        if (!bordersField(borderingField)) {
            throw new IllegalArgumentException(String.format(
                    "The passed field (%s) doesn't lie on either of the borders of the MazeField object (%s).",
                    borderingField, this
            ));
        }
        if (borderingField.positionY < positionY) {
            return BorderingFieldSide.TOP;
        } else if (borderingField.positionX > positionX) {
            return BorderingFieldSide.RIGHT;
        } else if (borderingField.positionY > positionY) {
            return BorderingFieldSide.BOTTOM;
        }  else {
            return BorderingFieldSide.LEFT;
        }
    }

    /**
     * Returns {@code true} if the passed field borders this {@link MazeField} object, {@code false} otherwise.
     *
     * @param borderingField The object to compare this maze field object with
     * @return as described above
     */
    public boolean bordersField(MazeField borderingField) {
        return borderingField != null
                && (Math.abs(positionX - borderingField.positionX)
                + Math.abs(positionY - borderingField.positionY)) == 1;
    }
}