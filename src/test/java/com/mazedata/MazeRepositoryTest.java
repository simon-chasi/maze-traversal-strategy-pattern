package com.mazedata;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

public class MazeRepositoryTest {
    private static final Logger LOGGER = Logger.getLogger(MazeRepository.class.getName());
    private static final TestHandler testHandler = new TestHandler();
    private static final String NULL_OR_BLANK_PATH_LOG_TEXT
            = "A null or blank file path has been provided. No mazes could be imported.";

    @TempDir
    File tempDir;

    private MazeRepository mrWithValidFileType;
    private MazeRepository mrWithInvalidFileType;


    @BeforeAll
    static void setUpLoggerAndHandler() {
        LOGGER.addHandler(testHandler);
        LOGGER.setUseParentHandlers(false); // Avoid duplicate logging
    }

    @BeforeEach
    void setUpHandlerFilesAndRepository() throws IOException {
        testHandler.reset();

        File validMazeFile = new File(tempDir, "mazes.txt");
        Files.write(
                validMazeFile.toPath(),
                List.of(
                        "-new-maze-",
                        "Maze 1",
                        "",         // Empty lines
                        "    ",     // which should
                        "\n\r\t",   // be skipped
                        "#####",
                        "#oSo#",
                        "##oo#",
                        "##E##",
                        "-new-maze-",
                        "Maze 2",
                        "###o",
                        "#Eoo",
                        "#ooo"
                )
        );

        File invalidMazeFile = new File(tempDir, "mazes.bin");
        Files.write(invalidMazeFile.toPath(), new byte[] { 1, 2, 3, 4 });

        mrWithValidFileType = new MazeRepository(validMazeFile.getAbsolutePath());
        mrWithInvalidFileType = new MazeRepository(invalidMazeFile.getAbsolutePath());
    }

    @Test
    void testFileTypeIsCorrect_WithValidFileType() {
        assertTrue(mrWithValidFileType.fileTypeIsCorrect(), "Expected correct file type for text/plain.");
    }

    @Test
    void testFileTypeIsCorrect_WithInvalidFileType() {
        assertFalse(mrWithInvalidFileType.fileTypeIsCorrect(), "Expected false for invalid file type.");
    }

    @Test
    void testReadMazeFileAndStoreLines_WithValidFileType() {
        mrWithValidFileType.readMazeFileAndStoreLines();
        List<String> lines = mrWithValidFileType.getMazeFileLines();

        assertEquals(11, lines.size());
        assertEquals("-new-maze-", lines.get(0));
        assertEquals("Maze 1", lines.get(1));
        assertEquals("#####", lines.get(2));
        assertEquals("#oSo#", lines.get(3));
        assertEquals("##oo#", lines.get(4));
        assertEquals("##E##", lines.get(5));
        assertEquals("-new-maze-", lines.get(6));
        assertEquals("Maze 2", lines.get(7));
        assertEquals("###o", lines.get(8));
        assertEquals("#Eoo", lines.get(9));
        assertEquals("#ooo", lines.get(10));
    }

    @Test
    void testReadMazeFileAndStoreLines_WithInvalidFileType() {
        mrWithInvalidFileType.readMazeFileAndStoreLines();
        testHandler.assertMessageIsLogged("The file from which mazes are to be imported must be of type text/plain.");
    }

    @Test
    void testReadMazeFileAndStoreLines_WithNullFilePath() {
        MazeRepository mrWithNullFilePath = new MazeRepository(null);
        mrWithNullFilePath.readMazeFileAndStoreLines();
        testHandler.assertMessageIsLogged(NULL_OR_BLANK_PATH_LOG_TEXT);
    }

    @Test
    void testReadMazeFileAndStoreLines_WithBlankFilePath() {
        MazeRepository mrWithBlankFilePath = new MazeRepository("  \t\r\n   ");
        mrWithBlankFilePath.readMazeFileAndStoreLines();
        testHandler.assertMessageIsLogged(NULL_OR_BLANK_PATH_LOG_TEXT);
    }

    @Test
    void testReadMazeFileAndStoreLines_WithInvalidFilePath() {
        String filePath = "does/not/exist/mazes.txt";
        MazeRepository mrWithInvalidFilePath = new MazeRepository(filePath);
        mrWithInvalidFilePath.readMazeFileAndStoreLines();
        testHandler.assertMessageIsLogged("Unable to read file using file path: " + filePath);
    }

    @Test
    void testImportMazes_CreatesTwoMazes_WithCorrectArguments() {
        mrWithValidFileType.readMazeFileAndStoreLines();

        try (MockedStatic<Maze.MazeFactory> mockedFactory = Mockito.mockStatic(Maze.MazeFactory.class)) {

            Maze mockMaze1 = Mockito.mock(Maze.class);
            Maze mockMaze2 = Mockito.mock(Maze.class);

            // Configure returned maze objects in the order they are expected to be created
            mockedFactory.when(() -> Maze.MazeFactory.createMaze(
                    Mockito.any(String[].class),
                    Mockito.eq("Maze 1")
            )).thenReturn(mockMaze1);

            mockedFactory.when(() -> Maze.MazeFactory.createMaze(
                    Mockito.any(String[].class),
                    Mockito.eq("Maze 2")
            )).thenReturn(mockMaze2);

            mrWithValidFileType.importMazes();
            assertEquals(2, mrWithValidFileType.getMazes().size());

            // Argument verification
            mockedFactory.verify(() ->
                    Maze.MazeFactory.createMaze(
                            new String[] {
                                    "#####",
                                    "#oSo#",
                                    "##oo#",
                                    "##E##"
                            },
                            "Maze 1"
                    )
            );

            mockedFactory.verify(() ->
                    Maze.MazeFactory.createMaze(
                            new String[] {
                                    "###o",
                                    "#Eoo",
                                    "#ooo"
                            },
                            "Maze 2"
                    )
            );
        }
    }

    @Test
    void testImportMazes_NoImportWhenFileLinesEmpty() {
        MazeRepository mrNoImport = new MazeRepository("some/path");

        try (MockedStatic<Maze.MazeFactory> mockedFactory = Mockito.mockStatic(Maze.MazeFactory.class)) {
            mrNoImport.importMazes(true);
            mockedFactory.verifyNoInteractions();
        }
    }

    @Test
    void testImportMazes_ClearsPreviousMazesWhenFlagIsTrue() {
        mrWithValidFileType.readMazeFileAndStoreLines();

        try (MockedStatic<Maze.MazeFactory> mockedFactory = Mockito.mockStatic(Maze.MazeFactory.class)) {

            Maze mockMaze = Mockito.mock(Maze.class);
            mockedFactory.when(() -> Maze.MazeFactory.createMaze(Mockito.any(), Mockito.any()))
                    .thenReturn(mockMaze);

            mrWithValidFileType.importMazes(true);
            assertEquals(2, mrWithValidFileType.getMazes().size());

            // Check that the mazes list was cleared
            mrWithValidFileType.importMazes(true);
            assertEquals(2, mrWithValidFileType.getMazes().size());

            mockedFactory.verify(
                    () -> Maze.MazeFactory.createMaze(Mockito.any(), Mockito.any()),
                    Mockito.times(4)
            );
        }
    }

}
