package com.mazedata;

import java.util.logging.Logger;

/**
 * <p>
 *     The class {@code Maze} represents a labyrinth which consists out of a board with a starting and ending field.
 *     It provides a static method for creating a maze object and overrides {@link Object#toString()}
 *     to provide a unique presentation of a maze in the console.
 * </p>
 * Based on the maze's description and look the user may choose a maze traversing strategies to traverse it.
 */
public class Maze {
    /**
     * Logger for tracking events and errors in the {@link Maze} class.
     */
    private static final Logger LOGGER = Logger.getLogger(Maze.class.getName());

    private final String description;

    // Characters representing either a path, a wall, an empty field or
    // the starting and ending field of a maze stored as text
    private static final char PATH = 'o';
    private static final char WALL = '#';
    private static final char STARTING_FIELD = 'S';
    private static final char ENDING_FIELD = 'E';
    private static final char EMPTY_FIELD = ' ';

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
        StringBuilder mazeBoardAsText = new StringBuilder();
        String lineSeparator = System.lineSeparator();

        for (boolean[] row : mazeBoard) {
            for (boolean field : row) {
                mazeBoardAsText.append(field ? PATH : WALL);
            }
            mazeBoardAsText.append(lineSeparator);
        }

        // Replace starting and ending field characters only after initializing the board with path and
        // wall characters. Performing a further check in the nested for-loop is costly for large mazes.
        int mazeBoardAsTextWidth = mazeBoard[0].length + lineSeparator.length();
        int startingFieldCharIndex = startingField.calculateSequenceInBoard(mazeBoardAsTextWidth);
        int endingFieldCharIndex = endingField.calculateSequenceInBoard(mazeBoardAsTextWidth);

        mazeBoardAsText.setCharAt(startingFieldCharIndex, STARTING_FIELD);
        mazeBoardAsText.setCharAt(endingFieldCharIndex, ENDING_FIELD);

        return description == null ? "No description provided" : description
                + lineSeparator.repeat(2) + mazeBoardAsText;
    }

    /**
     * The {@code static} nested {@code MazeFactory} class is responsible for creating new {@link Maze}
     * objects and provides the static method {@link #createMaze(String[], String)}.
     */
    public static class MazeFactory {
        /**
         * Performs checks to ensure that the passed field lines array is valid and eventually
         * parses the lines along with the description to a {@link Maze} object.
         *
         * @param fieldLines A string array containing the horizontal maze field lines
         * @param description The maze description
         * @return A successfully created {@link Maze} object or {@code null} if the initialization fails
         */
        public static Maze createMaze(String[] fieldLines, String description) {
            if (fieldLines == null || fieldLines.length < 1) {
                LOGGER.warning(String.format(
                        "To create a maze (description: \"%s\") the provided field lines array must not be null or empty.",
                        description
                ));
                return null;
            }

            if (!onlyOneStartingAndEndingFieldCharPresent(fieldLines)) {
                return null;
            }

            boolean[][] mazeBoard = determineMazeBoard(fieldLines);
            if (mazeBoard == null) {
                return null;
            }

            return parseLinesAndCreateMaze(fieldLines, mazeBoard, description);
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

            if (fieldLinesInOneLine.indexOf(STARTING_FIELD)
                    != fieldLinesInOneLine.lastIndexOf(STARTING_FIELD)) {
                LOGGER.warning(
                        "Detected none or more than one starting field characters in the passed field lines array:"
                                + String.join(System.lineSeparator(), fieldLines)
                );
                return false;
            }
            if (fieldLinesInOneLine.indexOf(ENDING_FIELD)
                    != fieldLinesInOneLine.lastIndexOf(ENDING_FIELD)) {
                LOGGER.warning(
                        "Detected none or more than one ending field characters in the passed field lines array:"
                                + String.join(System.lineSeparator(), fieldLines)
                );
                return false;
            }
            return true;
        }

        /**
         * <p>
         *     Determines the width and height of the 2d maze board and returns a new 2d boolean array.
         * </p>
         * The maze board width is determined by the longest string in the provided string array while
         * the maze height is specified by the provided array's length.
         * If the array contains null objects or empty strings, the height is reduced accordingly.
         *
         * @param fieldLines A non-null and non-empty string array containing the horizontal maze field lines
         * @return as described above
         */
        private static boolean[][] determineMazeBoard(String[] fieldLines) {
            int mazeWidth = 0;
            int mazeHeight = fieldLines.length;

            for (String fieldsLine : fieldLines) {
                if (fieldsLine == null || fieldsLine.isBlank()) {
                    mazeHeight--;
                    continue;
                }
                // The leading white spaces are not removed with the trailing ones
                // in order to enable the representation of elliptical mazes.
                mazeWidth = Math.max(mazeWidth, fieldsLine.stripTrailing().length());
            }

            if (mazeWidth == 0 || mazeHeight == 0) {
                LOGGER.warning(String.format(
                        "A maze board with a zero width or height is not allowed. "
                                + "Calculated width: %d, calculated height: %d",
                        mazeWidth, mazeHeight
                ));
                return null;
            }

            return new boolean[mazeHeight][mazeWidth];
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

            for (int i = 0; i < fieldLines.length; i++) {
                String fieldLine = fieldLines[i];
                if (fieldLine == null || fieldLine.isBlank()) {
                    continue;
                }

                for (int j = 0; j < fieldLine.length(); j++) {
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

            return createMazeWithParsedLines(mazeBoard, startingField, endingField, description);
        }

        /**
         * Attempts to initialize a new {@link Maze} object out of passed (parsed) arguments.
         *
         * @param mazeBoard The maze board with {@code true} values representing a path
         *                  and {@code false} values representing a wall
         * @param startingField A non-null starting {@link MazeField}
         * @param endingField A non-null starting {@link MazeField}
         * @param description The maze description
         * @return A new {@link Maze} instance or {@code null} if initialization fails
         */
        private static Maze createMazeWithParsedLines(boolean[][] mazeBoard, MazeField startingField, MazeField endingField, String description) {
            if (startingField == null || endingField == null) {
                LOGGER.warning(String.format(
                        "No starting or ending field or both present in the maze (description: \"%s\").",
                        description
                ));
                return null;
            }

            try {
                return new Maze(mazeBoard, startingField, endingField, description);
            } catch (IllegalArgumentException e) {
                LOGGER.warning(e.getMessage());
                return null;
            }
        }
    }
}