package com.strategies;

import com.mazedata.Maze;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractMazeTraversalStrategyTest<T extends MazeTraversalStrategy> {
    protected T traversalStrategy;

    protected abstract T createMazeTraversalStrategy();
    protected abstract Stream<Arguments> testTraverseMaze_WithTraversableMazeCases();
    protected abstract Stream<Arguments> testTraverseMaze_WithUntraversableMazeCases();

    @BeforeEach
    public void setUp() {
        traversalStrategy = createMazeTraversalStrategy();
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("testTraverseMaze_WithTraversableMazeCases")
    void testTraverseMaze_WithTraversableMaze(Maze maze, boolean[][] expectedTraversedBoard) {
        boolean[][] actualTraversedBoard = traversalStrategy.traverseMaze(maze);

        assertArrayEquals(
                expectedTraversedBoard,
                actualTraversedBoard,
                String.join(
                        System.lineSeparator(),
                        String.format(
                                "The traversed maze board using the %s maze traversal strategy:",
                                traversalStrategy.getClass().getSimpleName()
                        ),
                        maze.toString(),
                        "was expected to look like this:",
                        maze.traversedBoardToString(expectedTraversedBoard),
                        "Actual traversed board:",
                        maze.traversedBoardToString(actualTraversedBoard)
                )
        );
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("testTraverseMaze_WithUntraversableMazeCases")
    void testTraverseMaze_WithUntraversableMaze(Maze maze, String reason) {
        MazeNotTraversableException exception = assertThrows(
                MazeNotTraversableException.class,
                () -> traversalStrategy.traverseMaze(maze),
                String.format(
                        "A %s should be thrown when traversing a maze which is not traversable using %s.",
                        MazeNotTraversableException.class.getSimpleName(), traversalStrategy.getClass().getSimpleName()
                )
        );
        // If the passed reason is null it needs not to be checked
        if (reason != null) {
            assertTrue(
                    exception.getReason().contains(reason),
                    String.format(
                            "The reason \"%s\" should be contained in the exception. Actual reason: \"%s\"",
                            reason, exception.getReason()
                    )
            );
        }
    }
}
