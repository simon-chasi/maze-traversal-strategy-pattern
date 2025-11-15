package com.mazedata;

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
     * Returns the sequence of this field in a board of the given width.
     *
     * @param boardWidth The width of the board this field is part of
     * @return as described above
     */
    public int calculateSequenceInBoard(int boardWidth) {
        return positionX + boardWidth * positionY;
    }
}
