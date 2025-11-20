package com.mazedata;

import java.util.Arrays;
import java.util.logging.Logger;

/**
 * <p>
 *     The class {@code Maze} represents a labyrinth which consists out of a board with a starting and ending field.
 *     It provides a static class with a static method for creating a maze object and contains string returning methods
 *     to provide various presentations of a maze in the console.
 * </p>
 * Based on the maze's description and look the user may choose a maze traversing strategies to traverse it.
 */
public class Maze {
    /**
     * Logger for tracking events and errors in the {@link Maze} class.
     */
    private static final Logger LOGGER = Logger.getLogger(Maze.class.getName());

    private final String description;

    // Characters representing either a path, a wall, an empty field, the starting
    // and ending field or the traversed path of a maze stored as text
    private static final char PATH = 'o';
    private static final char WALL = '#';
    private static final char EMPTY_FIELD = ' ';
    private static final char STARTING_FIELD = 'S';
    private static final char ENDING_FIELD = 'E';
    private static final char TRAVERSED_PATH = 'x';

    /**
     * The maze board is stored as a 2d boolean array. {@code true} values represent the path
     * while {@code false} values indicate that a wall is to be found at the specific position.
     */
    private final boolean[][] mazeBoard;

    private final MazeField startingField;
    private final MazeField endingField;

    /**
     * Constructs a {@link Maze} objects taking into account the underlying conditions.
     *
     * @param mazeBoard A non-null and non-empty 2d boolean array with initialized values for the path and walls
     * @param startingField A non-null {@link MazeField} object which represents the starting position of the maze
     * @param endingField A non-null {@link MazeField} object which represents the ending position of the maze
     * @param description An optional maze description
     * @throws IllegalArgumentException <ul>
     *                                      <li>If any of the arguments except the description are {@code null}</li>
     *                                      <li>the maze board's height or width is zero</li>
     *                                      <li>the starting or ending field(s) lie(s) outside the board</li>
     *                                  </ul>
     */
    public Maze(boolean[][] mazeBoard, MazeField startingField, MazeField endingField, String description)
            throws IllegalArgumentException {
        if (mazeBoard == null || mazeBoard.length < 1 || mazeBoard[0] == null || mazeBoard[0].length < 1) {
            throw new IllegalArgumentException(
                    "To successfully create a maze object the passed maze board "
                            + "must not be null and have at least one entry."
            );
        }

        if (startingField == null || endingField == null) {
            throw new IllegalArgumentException(
                    "To successfully create a maze object the passed starting and ending fields must not be null."
            );
        }

        if (startingField.positionX() >= mazeBoard[0].length || startingField.positionY() >= mazeBoard.length
                || endingField.positionX() >= mazeBoard[0].length || endingField.positionY() >= mazeBoard.length) {
            throw new IllegalArgumentException(String.format(
                    "To successfully create a maze object the passed starting and ending fields "
                            + "must lie within the maze board.%sStarting field: %s, ending field: %s\n"
                            + "Maze board width: %d, maze board height: %d",
                    System.lineSeparator().repeat(2), startingField, endingField,
                    mazeBoard[0].length, mazeBoard.length
            ));
        }

        this.mazeBoard = mazeBoard;
        this.startingField = startingField;
        this.endingField = endingField;
        this.description = description;
    }

    // Getters
    public boolean[][] getMazeBoard() { return mazeBoard; }
    public MazeField getStartingField() { return startingField; }
    public MazeField getEndingField() { return endingField; }
    public String getDescription() { return description; }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String toString() {
        return description == null ? "No description provided" : description
                + System.lineSeparator().repeat(2) + mazeBoardToString();
    }

    /**
     * Returns a string representation of the maze board.
     */
    public String mazeBoardToString() {
        return boardToString(null);
    }

    /**
     * Returns a string representation of board which can traverse this maze. The passed board
     * doesn't necessarily need to successfully traverse the maze. It can also show that the
     * maze is untraversable if a path from the starting to the ending field doesn't exist.
     *
     * @param traversedBoard The traversed maze board with {@code true} values representing the traversed path
     * @return The maze board along with the traversed board as string or {@code null} if
     *         the passed board's dimensions don't correspond with this object's maze board
     */
    public String traversedBoardToString(boolean[][] traversedBoard) {
        if (traversedBoard == null || traversedBoard.length != mazeBoard.length
                || traversedBoard[0] == null || traversedBoard[0].length != mazeBoard[0].length) {
            LOGGER.warning(
                    "To return the traversed maze board as string it must not be null "
                            + "and it must have the same dimensions as the maze board."
            );
            return null;
        }

        return boardToString(traversedBoard);
    }

    /**
     * <p>
     *     Returns a string representation of the maze board if {@code traversedBoard == null}, otherwise
     *     the traversed board with {@code true} values standing for the traversed path is returned.
     * </p>
     * Being private this method doesn't perform checks to avoid exceptions (see below), so in order for
     * exceptions not to be thrown, tha passed board must either be {@code null} or have the same height
     * and width as the maze board while none of the {@code boolean[]} rows is {@code null}.
     *
     * @param traversedBoard The traversed board or {@code null}
     * @return as described above
     * @throws NullPointerException If one of the traversed board {@code boolean[]} rows is {@code null}
     * @throws ArrayIndexOutOfBoundsException If the traversed board doesn't have the same dimensions as
     *                                        this object's maze board
     */
    private String boardToString(boolean[][] traversedBoard) {
        StringBuilder boardAsString = new StringBuilder();

        for (int i = 0; i < mazeBoard.length; i++) {
            for (int j = 0; j < mazeBoard[0].length; j++) {
                boardAsString.append(
                        traversedBoard != null && traversedBoard[i][j]
                                ? TRAVERSED_PATH
                                : mazeBoard[i][j] ? PATH : WALL
                );
            }
            boardAsString.append(System.lineSeparator());
        }

        // Replace starting and ending field characters only after initializing the board with path and
        // wall characters. Performing a further check in the nested for-loop is costly for large mazes.
        int boardAsStringWidth = mazeBoard[0].length + System.lineSeparator().length();
        boardAsString.setCharAt(startingField.calculateSequenceInBoard(boardAsStringWidth), STARTING_FIELD);
        boardAsString.setCharAt(endingField.calculateSequenceInBoard(boardAsStringWidth), ENDING_FIELD);

        return boardAsString.toString();
    }

    /**
     * The {@code static} nested {@code MazeFactory} class is responsible for creating new {@link Maze}
     * objects and provides the static method {@link #createMaze(String[], String)}.
     */
    public static class MazeFactory {
        /**
         * Logger for tracking events and errors in the {@link MazeFactory} class.
         */
        private static final Logger LOGGER = Logger.getLogger(MazeFactory.class.getName());

        /**
         * Performs checks to ensure that the passed field lines array is valid and eventually
         * parses the lines along with the description to a {@link Maze} object.
         *
         * @param fieldLines A string array containing the horizontal maze field lines
         * @param description The maze description
         * @return A successfully created {@link Maze} object or {@code null} if the initialization fails
         */
        public static Maze createMaze(String[] fieldLines, String description) {
            if (fieldLinesInvalid(fieldLines, description) ||
                    !onlyOneStartingAndEndingFieldCharPresent(fieldLines)) {
                return null;
            }

            int mazeWidth = 0;
            for (String fieldsLine : fieldLines) {
                // The leading white spaces are not removed with the trailing ones
                // in order to enable the representation of elliptical mazes.
                mazeWidth = Math.max(mazeWidth, fieldsLine.stripTrailing().length());
            }

            boolean[][] mazeBoard = new boolean[fieldLines.length][mazeWidth];

            return parseLinesAndCreateMaze(fieldLines, mazeBoard, description);
        }

        /**
         * Returns {@code true} if the field lines array is invalid, meaning that it's
         * either {@code null}, empty or contains {@code null} or blank strings.
         *
         * @param fieldLines A string array containing the horizontal maze field lines
         * @param description The maze description (used only for logging)
         * @return as described above
         */
        private static boolean fieldLinesInvalid(String[] fieldLines, String description) {
            if (fieldLines == null || fieldLines.length < 1
                    || Arrays.stream(fieldLines).anyMatch(line -> line == null || line.isBlank())) {
                LOGGER.warning(String.format(
                        "To create a maze (description: \"%s\") the provided field lines array must not "
                                + "be null, empty or contain null or blank strings.",
                        description
                ));
                return true;
            }
            return false;
        }

        /**
         * Returns {@code true} if there is only one starting and ending field char present
         * in the given field lines array, {@code false} otherwise.
         *
         * @param fieldLines A non-null and non-empty string array containing the horizontal maze field lines
         * @return as described above
         */
        private static boolean onlyOneStartingAndEndingFieldCharPresent(String[] fieldLines) {
            String fieldLinesInOneLine = String.join("", fieldLines);

            boolean onlyOneStartingFieldCharPresent = fieldLinesInOneLine.chars()
                    .filter(ch -> ch == STARTING_FIELD).count() == 1;
            boolean onlyOneEndingFieldCharPresent = fieldLinesInOneLine.chars()
                    .filter(ch -> ch == ENDING_FIELD).count() == 1;

            if (onlyOneStartingFieldCharPresent && onlyOneEndingFieldCharPresent) {
                return true;
            }
            LOGGER.warning(
                    "Detected none or more than one starting or / and ending field characters "
                            + "in the passed field lines array:"
                            + System.lineSeparator() + String.join(System.lineSeparator(), fieldLines)
            );
            return false;
        }

        /**
         * Parses each character of the given field lines array to a path, wall, starting or ending field
         * and returns a new {@link Maze} instance or {@code null} if the parsing fails.
         *
         * @param fieldLines A non-null and non-empty string array containing the horizontal maze field lines
         * @param mazeBoard The maze board to be filled with values
         * @param description The maze description
         * @return as described above
         */
        private static Maze parseLinesAndCreateMaze(String[] fieldLines, boolean[][] mazeBoard, String description) {
            MazeField startingField = null;
            MazeField endingField = null;

            for (int i = 0; i < mazeBoard.length; i++) {
                String fieldLine = fieldLines[i];

                // Using Math.min for the upper bound secures that ArrayIndexOutOfBoundsException is not thrown
                for (int j = 0; j < Math.min(mazeBoard[0].length, fieldLine.length()); j++) {
                    switch (fieldLine.charAt(j)) {
                        case PATH -> mazeBoard[i][j] = true;
                        case STARTING_FIELD -> {
                            mazeBoard[i][j] = true;
                            startingField = new MazeField(j, i);
                        }
                        case ENDING_FIELD -> {
                            mazeBoard[i][j] = true;
                            endingField = new MazeField(j, i);
                        }
                        case WALL, EMPTY_FIELD -> mazeBoard[i][j] = false;
                        default -> {
                            LOGGER.warning(String.format(
                                    "Detected invalid character \"%c\" at line %d \"%s\" in the following maze:%s%s",
                                    fieldLine.charAt(j), i + 1, fieldLine, System.lineSeparator(),
                                    String.join(System.lineSeparator(), fieldLines)
                            ));
                            return null;
                        }
                    }
                }
            }
            // No need to wrap with try and catch block since at this point all arguments are meet the conditions
            return new Maze(mazeBoard, startingField, endingField, description);
        }
    }
}