package com.strategies;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class BreadthFirstSearchTest {
    @Nested
    public class TraversableCheck extends AbstractMazeTraversableCheckStrategyTest<BreadthFirstSearch> {
        @Override
        protected BreadthFirstSearch createTraversableCheckStrategy() {
            return new BreadthFirstSearch();
        }
    }

    @Nested
    public class TraverseMaze extends AbstractMazeTraversalStrategyTest<BreadthFirstSearch> {

        @Override
        protected BreadthFirstSearch createMazeTraversalStrategy() {
            return new BreadthFirstSearch();
        }

        @Override
        protected Stream<Arguments> testTraverseMaze_WithTraversableMazeCases() {
            return Stream.of(
                    Arguments.of(
                            TestMazeObjects.TRAVERSABLE_WITH_3_PATHS_OF_DIFFERENT_LENGTH,
                            new boolean[][] {
                                    { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false },
                                    { false, false, false, false, false, true , false, false, false, false, false, false, false, false, false },
                                    { false, false, false, false, false, true , false, false, false, false, false, false, false, false, false },
                                    { false, false, false, false, false, true , true , true , false, false, false, false, false, false, false },
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
                    Arguments.of(
                            TestMazeObjects.TRAVERSABLE_BUT_NOT_WITH_EITHER_HAND_ON_WALL,
                            new boolean[][] {
                                    { false, false, false, false, false, false, false, false, false, false, false, false, false },
                                    { false, false, false, false, false, false, false, false, false, false, false, false, false },
                                    { false, false, false, true , false, false, false, false, false, false, false, false, false },
                                    { false, false, true , true , false, false, false, false, false, false, false, false, false },
                                    { false, false, true , false, false, false, false, false, false, false, false, false, false },
                                    { false, true , true , false, false, false, false, false, false, false, false, false, false },
                                    { false, false, false, false, false, false, false, false, false, false, false, false, false },
                                    { false, false, false, false, false, false, false, false, false, false, false, false, false },
                                    { false, false, false, false, false, false, false, false, false, false, false, false, false },
                            }
                    ),
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
                                    { false, false, false },
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
