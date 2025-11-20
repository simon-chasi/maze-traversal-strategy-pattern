package com.mazedata;

import com.mazedata.Maze.MazeFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class MazeTest {
    /**
     * Maze description can be {@code null} or have a value, nonetheless it's content is irrelevant for testing.
     */
    private static final String description = "Some description";


    @Nested
    public class MazeFactoryTest {
        private static final Logger LOGGER = Logger.getLogger(MazeFactory.class.getName());
        private static final TestHandler testHandler = new TestHandler();

        // Used a couple of times and extracted into constants
        private static final String FIELD_LINES_INITIAL_CHECK_LOG_TEXT
                = "field lines array must not be null, empty or contain null or blank strings";
        private static final String NONE_OR_MORE_THAN_ONE_STARTING_FIELD_LOG_TEXT
                = "Detected none or more than one starting or / and ending field characters";

        @BeforeAll
        static void setUp() {
            LOGGER.addHandler(testHandler);
            LOGGER.setUseParentHandlers(false); // Avoid duplicate logging
        }

        @BeforeEach
        void resetHandler() {
            testHandler.reset();
        }

        @AfterAll
        static void tearDown() {
            LOGGER.removeHandler(testHandler);
        }

        @ParameterizedTest(name = "{index}: fieldLines={0}, assertMessage={1}, logMessage={2}")
        @MethodSource("createMaze_ShouldLogErrorAndReturnNullCases")
        void createMaze_ShouldLogErrorAndReturnNull(
                String[] fieldLines, String assertMessage, String logMessage
        ) {
            assertNull(
                    Maze.MazeFactory.createMaze(fieldLines, description),
                    assertMessage
            );
            testHandler.assertMessageIsLogged(logMessage);
        }

        private static Stream<Arguments> createMaze_ShouldLogErrorAndReturnNullCases() {
            return Stream.of(
                    Arguments.of(
                            null,
                            "When passing a null value for the fieldLines array, the return value must be null.",
                            FIELD_LINES_INITIAL_CHECK_LOG_TEXT
                    ),
                    Arguments.of(
                            new String[0],
                            "When passing an empty fieldLines array, the return value must be null.",
                            FIELD_LINES_INITIAL_CHECK_LOG_TEXT
                    ),
                    Arguments.of(
                            new String[] { "##", null, "oo#", null },
                            "When passing a fieldLines array with null values, the return value must be null.",
                            FIELD_LINES_INITIAL_CHECK_LOG_TEXT
                    ),
                    Arguments.of(
                            new String[] { "", " \r" },
                            "When passing a fieldLines array with blank values, the return value must be null.",
                            FIELD_LINES_INITIAL_CHECK_LOG_TEXT
                    ),
                    Arguments.of(
                            new String[] { "#o#", "o##", "#" },
                            "When passing a fieldLines array with no starting or ending "
                                    + "field char, the return value must be null.",
                            NONE_OR_MORE_THAN_ONE_STARTING_FIELD_LOG_TEXT
                    ),
                    Arguments.of(
                            new String[] { "#o#", "o##", "#E" },
                            "When passing a fieldLines array with no starting "
                                    + "field char, the return value must be null.",
                            NONE_OR_MORE_THAN_ONE_STARTING_FIELD_LOG_TEXT
                    ),
                    Arguments.of(
                            new String[] { "#o#S", "o##", "#" },
                            "When passing a fieldLines array with no ending "
                                    + "field char, the return value must be null.",
                            NONE_OR_MORE_THAN_ONE_STARTING_FIELD_LOG_TEXT
                    ),
                    Arguments.of(
                            new String[] { "#S#", "oS" },
                            "When passing a fieldLines array with more than one starting "
                                    + "field char, the return value must be null.",
                            NONE_OR_MORE_THAN_ONE_STARTING_FIELD_LOG_TEXT
                    ),
                    Arguments.of(
                            new String[] { "#E#", "oE" },
                            "When passing a fieldLines array with more than one ending "
                                    + "field char, the return value must be null.",
                            NONE_OR_MORE_THAN_ONE_STARTING_FIELD_LOG_TEXT
                    ),
                    Arguments.of(
                            new String[] { "S", "#", "\tE" },
                            "When passing a fieldLines array with strings that contain invalid characters "
                                    + "at the beginning of a line, the return value must be null.",
                            "Detected invalid character"
                    ),
                    Arguments.of(
                            new String[] { "S", "#", "##\rE" },
                            "When passing a fieldLines array with strings that contain invalid characters "
                                    + "at the middle of a line, the return value must be null.",
                            "Detected invalid character"
                    ),
                    Arguments.of(
                            new String[] { "S", "#oo#i", "##E" },
                            "When passing a fieldLines array with strings that contain invalid characters "
                                    + "at the end of a line, the return value must be null.",
                            "Detected invalid character"
                    )
            );
        }

        @Test
        void createMaze_ValidFieldLines() {
            String[] validFieldLines = new String[] {
                    "   ###   ",
                    "oooooooE#",
                    " o#o#o#        ",
                    " o#o#ooo              ",
                    " ####oS"
            };

            boolean[][] expectedMazeBoard = new boolean[][] {
                    { false, false, false, false, false, false, false, false, false },
                    { true , true , true , true , true , true , true , true , false },
                    { false, true , false, true , false, true , false, false, false },
                    { false, true , false, true , false, true , true , true , false },
                    { false, false, false, false, false, true , true , false, false },
            };

            MazeField expectedStartingField = new MazeField(6, 4);
            MazeField expectedEndingField = new MazeField(7, 1);

            Maze actualMaze = Maze.MazeFactory.createMaze(validFieldLines, description);
            assert actualMaze != null;

            MazeField actualStartingField = actualMaze.getStartingField();
            MazeField actualEndingField = actualMaze.getEndingField();

            assertArrayEquals(
                    expectedMazeBoard,
                    actualMaze.getMazeBoard()
            );
            assertEquals(
                    expectedStartingField,
                    actualStartingField,
                    String.format(
                            "Expected starting field: %s, actual starting field: %s",
                            expectedStartingField, actualStartingField
                    )
            );
            assertEquals(
                    expectedEndingField,
                    actualEndingField,
                    String.format(
                            "Expected ending field: %s, actual ending field: %s",
                            expectedEndingField, actualEndingField
                    )
            );
            assertEquals(description, actualMaze.getDescription());
        }
    }
}
