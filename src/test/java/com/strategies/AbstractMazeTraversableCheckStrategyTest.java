package com.strategies;

import com.mazedata.Maze;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractMazeTraversableCheckStrategyTest<T extends MazeTraversableCheckStrategy> {
    protected T traversableCheckStrategy;

    protected abstract T createTraversableCheckStrategy();

    @BeforeEach
    public void setUp() {
        traversableCheckStrategy = createTraversableCheckStrategy();
    }

    @ParameterizedTest
    @MethodSource("com.strategies.TestMazeObjects#getTraversableMazes")
    void testMazeIsTraversable_WithTraversableMaze(Maze maze) {
        assertTrue(
                traversableCheckStrategy.mazeIsTraversable(maze),
                String.join(
                        System.lineSeparator(),
                        String.format(
                                "The following maze should be traversable using the %s traversal strategy:",
                                traversableCheckStrategy.getClass().getSimpleName()
                        ),
                        maze.toString()
                )
        );
    }

    @ParameterizedTest
    @MethodSource("com.strategies.TestMazeObjects#getUntraversableMazes")
    void testMazeIsTraversable_WithUntraversableMaze(Maze maze) {
        assertFalse(
                traversableCheckStrategy.mazeIsTraversable(maze),
                String.join(
                        System.lineSeparator(),
                        String.format(
                                "The following maze should not be traversable using the %s traversal strategy:",
                                traversableCheckStrategy.getClass().getSimpleName()
                        ),
                        maze.toString()
                )
        );
    }
}
