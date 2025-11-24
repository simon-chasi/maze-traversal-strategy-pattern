package com.mazedata;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>
 *     The class {@code MazeRepository} represents a storage containing {@link Maze} objects.
 *     These can be imported using the method {@link #importMazes(boolean)} after previously having read
 *     the maze file and storing the lines using {@link #readMazeFileAndStoreLines()}.
 * </p>
 * The maze file (type: {@value MAZE_FILE_TYPE}) consists of raw maze text data (description in one line
 * and maze fields in the following one or more lines) which is separated using {@value MAZE_LINES_SEPARATOR}
 * and should look like the following simple example:
 * <pre>
 * {@link #MAZE_LINES_SEPARATOR}
 * [First maze description]
 * [First maze fields]
 * {@link #MAZE_LINES_SEPARATOR}
 * [Second maze description]
 * [Second maze fields]
 * </pre>
 */
public class MazeRepository {
    /**
     * Logger for tracking events and errors in the {@link MazeRepository} class.
     */
    private static final Logger LOGGER = Logger.getLogger(MazeRepository.class.getName());

    /**
     * The file type of the file from which the mazes are imported.
     */
    private static final String MAZE_FILE_TYPE = "text/plain";

    /**
     * A string used to indicate a new maze in the file from which the mazes are imported.
     */
    private static final String MAZE_LINES_SEPARATOR = "-new-maze-";

    private final String mazeFilePath;

    /**
     * A list containing the maze file's data which is used to import {@link Maze} objects.
     */
    private final List<String> mazeFileLines;

    /**
     * A list containing the parsed {@link Maze} objects.
     */
    private final List<Maze> mazes;

    public MazeRepository(String mazeFilePath) {
        this.mazeFileLines = new ArrayList<>();
        this.mazes = new ArrayList<>();
        this.mazeFilePath = mazeFilePath;
    }

    /**
     * Reads the maze file and stores its lines which can then be processed in order to import mazes as {@link Maze}
     * objects. The file type must be equal to {@value MAZE_FILE_TYPE}, otherwise no mazes can be imported.
     */
    public void readMazeFileAndStoreLines() {
        if (mazeFilePath == null || mazeFilePath.isBlank()) {
            LOGGER.severe("A null or blank file path has been provided. No mazes could be imported.");
            return;
        }

        if (!fileTypeIsCorrect()) {
            return;
        }

        // Empty collection prior to the import to avoid duplicates
        mazeFileLines.clear();

        try (LineNumberReader lnr = new LineNumberReader(new FileReader(mazeFilePath))) {
            String line;
            while((line = lnr.readLine()) != null) {
                if (!line.isBlank()) {
                    mazeFileLines.add(line);
                } else {
                    LOGGER.info(String.format(
                            "Empty maze file line skipped at line number %d.", lnr.getLineNumber()
                    ));
                }
            }
        } catch (IOException e) {
            LOGGER.severe("Unable to read file using file path: " + mazeFilePath);
        }
    }

    /**
     * Checks if the maze file's type corresponds to the specified type {@value MAZE_FILE_TYPE}.
     *
     * @return {@code true} if the file type is correct, {@code false} otherwise
     */
    public boolean fileTypeIsCorrect() {
        try {
            String fileType = Files.probeContentType(new File(mazeFilePath).toPath());
            if (fileType != null && fileType.equals(MAZE_FILE_TYPE)) {
                return true;
            } else {
                LOGGER.severe(
                        String.format(
                                "The file from which mazes are to be imported must be of type %s. "
                                        + "The provided file's type is %s.",
                                MAZE_FILE_TYPE, fileType
                        )
                );
                return false;
            }
        } catch (IOException e) {
            LOGGER.severe(
                    "The file type of the provided file could not be checked. "
                            + "The file path is possibly invalid or incorrect."
            );
            return false;
        }
    }

    /**
     * Creates and adds new {@link Maze} objects to the mazes list.
     * The previously imported mazes are deleted prior to the import.
     *
     * @see #importMazes(boolean)
     */
    public void importMazes() {
        importMazes(true);
    }

    /**
     * Creates and adds new {@link Maze} objects to the mazes list by looping through maze file lines.
     * Each time the lines separator is detected, a new maze can be created.
     *
     * @param clearPrevious If {@code true} the mazes list is cleared before import
     * @see MazeRepository Maze file structure described in {@link MazeRepository}.
     */
    public void importMazes(boolean clearPrevious) {
        if (mazeFileLines.isEmpty()) {
            LOGGER.severe("Unable to import mazes. The maze file is possibly empty or has not yet been read.");
            return;
        }

        if (clearPrevious) {
            mazes.clear();
        }

        int lineIndex = 0;
        while (lineIndex < mazeFileLines.size()) {
            if (mazeFileLineIsNotSeparator(lineIndex)) {
                lineIndex++;
                continue;
            }

            lineIndex++; // Increase line index to get the maze description which comes directly after the separator
            if (lineIndex >= mazeFileLines.size()) {
                LOGGER.warning(String.format(
                        "The last line (%d) whose content is the separator \"%s\" has been reached "
                                + "while reading new maze data. No description or field lines can be extracted.",
                        lineIndex, MAZE_LINES_SEPARATOR
                ));
                return;
            }

            String mazeDescription = mazeFileLines.get(lineIndex).strip();
            lineIndex++; // Go to the next line directly in order to now gather the maze field lines

            List<String> mazeFieldLines = determineMazeFieldLines(lineIndex);
            lineIndex += mazeFieldLines.size();

            addMaze(Maze.MazeFactory.createMaze(
                    mazeFieldLines.toArray(new String[0]),
                    mazeDescription
            ));
        }
    }

    /**
     * <p>
     *     Returns {@code true} if the maze file line at the given index is
     *     not the specified separator line, {@code false} otherwise.
     * </p>
     * The line's white spaces are stripped in order to avoid unnecessary errors in the maze file.
     *
     * @param index The maze file line's index
     * @return as described above
     * @throws ArrayIndexOutOfBoundsException if {@code (index < 0 || index >= mazeFileLines.size())}
     */
    private boolean mazeFileLineIsNotSeparator(int index) {
        return !mazeFileLines.get(index).strip().equals(MAZE_LINES_SEPARATOR);
    }

    /**
     * Gathers the maze field lines starting from the passed index.
     *
     * @param index The maze file line's index
     * @return A list of maze field lines
     */
    private List<String> determineMazeFieldLines(int index) {
        List<String> mazeFieldLines = new ArrayList<>();
        while (index < mazeFileLines.size() && mazeFileLineIsNotSeparator(index)) {
            // Only cut trailing white spaces because some mazes may be elliptic
            mazeFieldLines.add(mazeFileLines.get(index).stripTrailing());
            index++;
        }
        return mazeFieldLines;
    }

    /**
     * Only this method should be used for adding a new {@link Maze} object
     * to the repository to prevent {@code null} values.
     *
     * @param maze A (non-null) {@link Maze} to be added
     */
    public void addMaze(Maze maze) {
        if (maze != null) {
            mazes.add(maze);
        }
    }

    public Maze getMaze(int index) { return mazes.get(index); }

    public List<Maze> getMazes() { return List.copyOf(mazes); }

    public List<String> getMazeFileLines() { return List.copyOf(mazeFileLines); }

    /**
     * Returns a string representation of the maze file lines. Used primarily for testing purposes.
     */
    public String mazeFileLinesToString() {
        return String.join(System.lineSeparator(), mazeFileLines);
    }

    /**
     * Returns a string representation of the imported mazes which can be shown at the start of the program.
     *
     * @return as described above
     */
    public String mazesToString() {
        StringBuilder mazesAsText = new StringBuilder();

        for (int i = 0; i < mazes.size(); i++) {
            mazesAsText.append(System.lineSeparator());
            mazesAsText.append(String.format("%d. %s", i + 1, mazes.get(i).toString()));
            mazesAsText.append(System.lineSeparator());
        }

        return mazesAsText.toString();
    }
}