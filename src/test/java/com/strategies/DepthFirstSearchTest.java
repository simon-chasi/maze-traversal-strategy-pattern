package com.strategies;

import com.mazedata.Maze;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class DepthFirstSearchTest {
    private DepthFirstSearch depthFirstSearch;

    @BeforeEach
    public void setUp() {
        depthFirstSearch = new DepthFirstSearch();
    }

    @ParameterizedTest
    @MethodSource("com.strategies.TestMazeObjects#getTraversableMazes")
    void testMazeIsTraversable_WithTraversableMaze(Maze maze) {
        assertTrue(
                depthFirstSearch.mazeIsTraversable(maze),
                String.join(
                        System.lineSeparator(),
                        "The following maze should be traversable using "
                                + "the depth first search traversal strategy:",
                        maze.toString()
                )
        );
    }
}
