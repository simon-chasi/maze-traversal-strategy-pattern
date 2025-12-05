package com.strategies;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class DepthFirstSearchTest {
    @Nested
    public class TraversableCheck extends GuaranteedMazeTraverserTest<DepthFirstSearch> {
        @Override
        protected DepthFirstSearch createGuaranteedMazeTraverser() {
            return new DepthFirstSearch();
        }
    }

    @Nested
    public class TraverseMaze extends AbstractMazeTraversalStrategyTest<DepthFirstSearch> {

        @Override
        protected DepthFirstSearch createMazeTraversalStrategy() {
            return new DepthFirstSearch();
        }

        @Override
        protected Stream<Arguments> testTraverseMaze_WithTraversableMazeCases() {
            return Stream.of(
                    Arguments.of(
                            TestMazeObjects.TRAVERSABLE_WITH_3_PATHS_OF_DIFFERENT_LENGTH,
                            new boolean[][] {
                                    { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false },
                                    { false, false, false, false, false, true , true , true , true , true , true , true , false, false, false },
                                    { false, false, false, false, false, false, false, false, false, false, false, true , false, false, false },
                                    { false, false, false, false, false, false, false, true , true , true , true , true , false, false, false },
                                    { false, false, false, false, false, false, false, true , false, false, false, false, false, false, false },
                                    { false, false, false, false, false, false, false, true , false, false, false, false, false, false, false },
                                    { false, false, false, false, false, false, false, true , false, false, false, false, false, false, false },
                                    { false, false, false, false, false, false, false, true , true , true , true , true , true , true , false },
                                    { false, false, false, false, false, false, false, false, false, false, false, false, false, true , false },
                                    { false, false, false, false, false, false, false, false, false, false, false, false, false, true , false },
                                    { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false },
                            }
                    ),
                    Arguments.of(
                            TestMazeObjects.TRAVERSABLE_WITH_2_PATHS_OF_SAME_LENGTH,
                            new boolean[][] {
                                    { false, false, false, false, false, false, false, false, false, false, false },
                                    { false, false, false, true , true , true , false, false, false, false, false },
                                    { false, false, false, true , false, true , false, false, false, false, false },
                                    { false, true , true , true , false, true , false, false, false, false, false },
                                    { false, true , false, false, false, true , false, false, false, false, false },
                                    { false, false, false, false, false, false, false, false, false, false, false },
                                    { false, false, false, false, false, false, false, false, false, false, false },
                                    { false, false, false, false, false, false, false, false, false, false, false },
                                    { false, false, false, false, false, false, false, false, false, false, false },
                            }
                    ),
                    // Check if a maze which is not traversable with hand on wall can be correctly traversed using DFS
                    Arguments.of(
                            TestMazeObjects.TRAVERSABLE_BUT_NOT_WITH_EITHER_HAND_ON_WALL,
                            new boolean[][] {
                                    { false, true , true , false, false, false, false, false, false, false, false, false, false },
                                    { false, true , true , true , true , true , true , true , true , true , true , true , false },
                                    { false, true , true , true , false, false, false, false, false, false, false, true , false },
                                    { false, true , true , false, false, false, false, false, false, false, false, true , false },
                                    { false, true , true , false, false, false, false, false, false, false, false, true , false },
                                    { false, true , true , true , true , true , true , true , true , true , true , true , false },
                                    { false, false, false, false, true , true , false, false, false, false, true , true , false },
                                    { false, false, false, false, true , true , false, false, false, false, false, false, false },
                                    { false, false, false, false, false, false, false, false, false, false, false, false, false },
                            }
                    ),
                    // Test trivial cases also
                    Arguments.of(
                            TestMazeObjects.TRAVERSABLE_WITH_START_AND_END_SAME,
                            new boolean[][] {
                                    { false, false, false },
                                    { false, true , false },
                                    { false, false, false },
                            }
                    ),
                    Arguments.of(
                            TestMazeObjects.TRAVERSABLE_WITH_START_AND_END_NEXT_TO_EACH_OTHER,
                            new boolean[][] {
                                    { true , true , false },
                                    { true , true , false },
                                    { false, false, false },
                            }
                    )
            );
        }

        @Override
        protected Stream<Arguments> testTraverseMaze_WithUntraversableMazeCases() {
            return TestMazeObjects.getUntraversableMazes().map(maze ->
                    Arguments.of(maze, null)
            );
        }
    }
}
