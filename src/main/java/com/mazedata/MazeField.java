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
    /**
     * <p>
     *     Used in combination with {@link BorderingFieldSide} this enum specifies the direction of bordering fields.
     * </p>
     * <table>
     *     <thead>
     *         <th>Clockwise</th>
     *         <th>Counterclockwise</th>
     *     </thead>
     *     <tbody>
     *         <tr>
     *             <td>
     *                 <pre>
     *   ----→ TOP  -----
     *   |              ↓
     * LEFT           RIGHT
     *   ↑              |
     *   ---- BOTTOM ←---
     *                 </pre>
     *             </td>
     *             <td>
     *                 <pre>
     *   ----- TOP  ←----
     *   ↓              |
     * LEFT           RIGHT
     *   |              ↑
     *   ---→ BOTTOM ----
     *                 </pre>
     *             </td>
     *         </tr>
     *     </tbody>
     * </table>
     */
    public enum BorderingFieldsDirection {
        CLOCKWISE(-1), COUNTER_CLOCKWISE(1);

        /**
         * An integer representation of the enum constant.
         */
        private final int direction;

        BorderingFieldsDirection(int direction) {
            this.direction = direction;
        }

        public int getDirection() { return direction; }

        /**
         * Returns the opposite direction.
         */
        public BorderingFieldsDirection inverse() {
            return direction == -1 ? COUNTER_CLOCKWISE : CLOCKWISE;
        }
    }

    /**
     * Contains the four borders of a field: TOP, RIGHT, BOTTOM, LEFT.
     *
     * @see BorderingFieldsDirection
     */
    public enum BorderingFieldSide {
        TOP, RIGHT, BOTTOM, LEFT;

        /**
         * Returns the four {@link BorderingFieldSide} enum constants starting from top in a clockwise direction.
         */
        public static BorderingFieldSide[] order() {
            return order(BorderingFieldsDirection.CLOCKWISE);
        }

        /**
         * Returns the four {@link BorderingFieldSide} enum constants starting from top in the given direction.
         */
        public static BorderingFieldSide[] order(BorderingFieldsDirection direction) {
            return new BorderingFieldSide[] {
                    TOP,
                    direction == BorderingFieldsDirection.CLOCKWISE ? RIGHT : LEFT,
                    BOTTOM,
                    direction == BorderingFieldsDirection.CLOCKWISE ? LEFT : RIGHT
            };
        }

        /**
         * Returns the ordinals of the enum constants ordered in a clockwise direction.
         */
        public static int[] orderedOrdinals() {
            return orderedOrdinals(BorderingFieldsDirection.CLOCKWISE);
        }

        /**
         * Returns the ordinals of the enum constants ordered in the given direction.
         */
        public static int[] orderedOrdinals(BorderingFieldsDirection direction) {
            return Arrays.stream(order(direction)).mapToInt(BorderingFieldSide::ordinal).toArray();
        }

        /**
         * Returns the next side in a clockwise direction.
         */
        public BorderingFieldSide next() {
            return next(1);
        }

        /**
         * Returns the side which is {@code distance} steps away from this one in a clockwise direction.
         */
        public BorderingFieldSide next(int distance) {
            return next(BorderingFieldsDirection.CLOCKWISE, distance);
        }

        /**
         * Returns the next side in the specified direction.
         */
        public BorderingFieldSide next(BorderingFieldsDirection direction) {
            return next(direction, 1);
        }

        /**
         * Returns the side which is {@code distance} steps away from this one in the specified directions.
         */
        public BorderingFieldSide next(BorderingFieldsDirection direction, int distance) {
            BorderingFieldSide[] values = BorderingFieldSide.values();
            return values[Math.floorMod(this.ordinal() - distance * direction.getDirection(), values.length)];
        }

         /**
         * Returns the previous side (next side in a counterclockwise direction).
         */
        public BorderingFieldSide previous() {
            return next(BorderingFieldsDirection.COUNTER_CLOCKWISE);
        }

        /**
         * Returns the side which is {@code distance} steps away from this one in a counterclockwise direction.
         */
        public BorderingFieldSide previous(int distance) {
            return next(BorderingFieldsDirection.COUNTER_CLOCKWISE, distance);
        }

        /**
         * Returns the side which lies opposite in comparison to this one.
         */
        public BorderingFieldSide inverse() {
            return next(2);
        }

        /**
         * <p>
         *     Returns four indexes for the bordering fields on the top, right, bottom and left side
         *     of a {@link MazeField} as an integer array.
         * </p>
         * The array's indexes are mapped to sides as following:
         * <ul>
         *     <li>0 -- Top</li>
         *     <li>1 -- Right</li>
         *     <li>2 -- Bottom</li>
         *     <li>3 -- Left</li>
         * </ul>
         *
         * @param direction Clockwise or counterclockwise starting from this side
         * @return as described above
         */
        public int[] indexes(BorderingFieldsDirection direction) {
            List<Integer> orderedOrdinals = new ArrayList<>(
                    Arrays.stream(orderedOrdinals(direction)).boxed().toList()
            );
            Collections.rotate(orderedOrdinals, this.ordinal());

            return orderedOrdinals.stream().mapToInt(Integer::intValue).toArray();
        }
    }

    public MazeField {
        if (positionX < 0 || positionY < 0) {
            throw new  IllegalArgumentException("MazeField position coordinates cannot be negative.");
        }
    }

    /**
     * Returns the four {@link MazeField}s which border this maze field as an array.
     * Starting from the bordering maze field on the given side the direction is clockwise.
     *
     * @param boardHeight The height of the board this field is part of
     * @param boardWidth The width of the board this field is part of
     * @param firstSide Top, right, bottom or left bordering field as the first array element.
     *                  Defaults to top if {@code null}.
     * @return as described above
     *
     * @see #determineBorderingFields(int, int, BorderingFieldsDirection, BorderingFieldSide)
     */
    public MazeField[] determineBorderingFields(int boardHeight, int boardWidth, BorderingFieldSide firstSide) {
        return determineBorderingFields(boardHeight, boardWidth, BorderingFieldsDirection.CLOCKWISE, firstSide);
    }

    /**
     * Returns the four {@link MazeField}s which border this maze field as an array.
     * The first element is the bordering field on the top.
     * The order of the following fields depends on the given direction.
     *
     * @param boardHeight The height of the board this field is part of
     * @param boardWidth The width of the board this field is part of
     * @param direction Clockwise or counterclockwise starting from the bordering field on the top.
     *                  Defaults to clockwise if {@code null}.
     * @return as described above
     *
     * @see #determineBorderingFields(int, int, BorderingFieldsDirection, BorderingFieldSide)
     */
    public MazeField[] determineBorderingFields(int boardHeight, int boardWidth, BorderingFieldsDirection direction) {
        return determineBorderingFields(boardHeight, boardWidth, direction, BorderingFieldSide.TOP);
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
     * @param direction Clockwise or counterclockwise starting from the given side.
     *                  Defaults to clockwise if {@code null}.
     * @param firstSide Top, right, bottom or left bordering field as the first array element.
     *                  Defaults to top if {@code null}.
     * @return as described above
     * @throws IllegalArgumentException if the maze field lies outside the given board dimensions
     */
    public MazeField[] determineBorderingFields(
            int boardHeight, int boardWidth, BorderingFieldsDirection direction, BorderingFieldSide firstSide
    ) {
        if (positionX >= boardWidth || positionY >= boardHeight) {
            throw new IllegalArgumentException(String.format(
                    "The bordering fields cannot be determined for %s which lies outside of the board.", this
            ));
        }

        MazeField[] borderingMazeFields = new MazeField[4];

        // calculateSequenceInBoard returns the sequence starting from 0,
        // but here a count from 1 is needed because of simpler modulo operations
        int mazeFieldSequence = calculateSequenceInBoard(boardWidth) + 1;

        int[] indexes = (firstSide == null ? BorderingFieldSide.TOP : firstSide)
                .indexes(direction == null ? BorderingFieldsDirection.CLOCKWISE : direction);

        // Maze field is not on the top edge
        if (mazeFieldSequence > boardWidth) {
            borderingMazeFields[indexes[0]] = new MazeField(positionX, positionY - 1);
        }
        // Maze field is not on the right edge
        if (mazeFieldSequence % boardWidth != 0) {
            borderingMazeFields[indexes[1]] = new MazeField(positionX + 1, positionY);
        }
        // Maze field is not on the bottom edge
        if (mazeFieldSequence <= (boardHeight - 1) * boardWidth) {
            borderingMazeFields[indexes[2]] = new MazeField(positionX, positionY + 1);
        }
        // Maze field is not on the left edge
        if (mazeFieldSequence % boardWidth != 1) {
            borderingMazeFields[indexes[3]] = new MazeField(positionX - 1, positionY);
        }

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
     * @param direction Clockwise or counterclockwise starting from the given side
     * @param firstSide Top, right, bottom or left as the first array element
     * @return as described above
     * @deprecated To determine the bordering fields indexes use
     *             {@link BorderingFieldSide#indexes(BorderingFieldsDirection)} instead.
     */
    private static int[] determineBorderingFieldsIndexes(
            BorderingFieldsDirection direction, BorderingFieldSide firstSide
    ) {
        List<Integer> indexes = new ArrayList<>(
                direction == BorderingFieldsDirection.CLOCKWISE
                        ? Arrays.asList(0, 1, 2, 3) // top (0), right (1), bottom (2), left (3)
                        : Arrays.asList(0, 3, 2, 1) // top (0), right (3), bottom (2), left (1)
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