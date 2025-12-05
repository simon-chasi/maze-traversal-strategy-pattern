package com.strategies;

import com.mazedata.Maze;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public abstract class GuaranteedMazeTraverserTest<T extends GuaranteedMazeTraverser> {
    protected T guaranteedMazeTraverser;

    protected abstract T createGuaranteedMazeTraverser();

    @BeforeEach
    public void setUp() {
        guaranteedMazeTraverser = createGuaranteedMazeTraverser();
    }

    @ParameterizedTest
    @MethodSource("getTraversableMazes")
    void testMazeIsTraversable_WithTraversableMaze(Maze maze) {
        assertTrue(
                guaranteedMazeTraverser.mazeIsTraversable(maze),
                String.join(
                        System.lineSeparator(),
                        String.format(
                                "The following maze should be traversable using the %s traversal strategy:",
                                guaranteedMazeTraverser.getClass().getSimpleName()
                        ),
                        maze.toString()
                )
        );
    }

    protected static Stream<Maze> getTraversableMazes() {
        return TestMazeObjects.getTraversableMazes();
    }

    @ParameterizedTest
    @MethodSource("getUntraversableMazes")
    void testMazeIsTraversable_WithUntraversableMaze(Maze maze) {
        assertFalse(
                guaranteedMazeTraverser.mazeIsTraversable(maze),
                String.join(
                        System.lineSeparator(),
                        String.format(
                                "The following maze should not be traversable using the %s traversal strategy:",
                                guaranteedMazeTraverser.getClass().getSimpleName()
                        ),
                        maze.toString()
                )
        );
    }

    protected static Stream<Maze> getUntraversableMazes() {
        return TestMazeObjects.getUntraversableMazes();
    }
}
