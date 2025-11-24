package com.strategies;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class HandOnWallTest {
    private static final HandOnWall.HandOnWallSide LEFT_SIDE = HandOnWall.HandOnWallSide.LEFT;
    private static final HandOnWall.HandOnWallSide RIGHT_SIDE = HandOnWall.HandOnWallSide.RIGHT;

    private static final String UNTRAVERSABLE_REASON_START_REACHED_TOO_MANY_TIMES = String.format(
            "The starting field has been reached %d times.",
            HandOnWall.getMaxStartingFieldReachedAmount()
    );

    private static final String UNTRAVERSABLE_REASON_FIELD_SURROUNDED
            = "is only surrounded by null fields or walls so that moving further is not possible";

    static Stream<Arguments> testTraverseMaze_WithTraversableMazeCases(
            HandOnWall.HandOnWallSide side, boolean[][][] expectedTraversedBoards
    ) {
        return Stream.of(
                Arguments.of(
                        TestMazeObjects.TRAVERSABLE_WITH_3_PATHS_OF_DIFFERENT_LENGTH,
                        expectedTraversedBoards[0]
                ),
                Arguments.of(
                        TestMazeObjects.TRAVERSABLE_WITH_2_PATHS_OF_SAME_LENGTH,
                        expectedTraversedBoards[1]
                ),
                Arguments.of(
                        side == LEFT_SIDE
                                ? TestMazeObjects.TRAVERSABLE_BUT_NOT_WITH_RIGHT_HAND_ON_WALL
                                : TestMazeObjects.TRAVERSABLE_BUT_NOT_WITH_LEFT_HAND_ON_WALL,
                        expectedTraversedBoards[2]
                ),
                Arguments.of(
                        TestMazeObjects.TRAVERSABLE_WITH_START_AND_END_SAME,
                        expectedTraversedBoards[3]
                ),
                Arguments.of(
                        TestMazeObjects.TRAVERSABLE_WITH_START_AND_END_NEXT_TO_EACH_OTHER,
                        expectedTraversedBoards[4]
                )
        );
    }

    static Stream<Arguments> testTraverseMaze_WithUntraversableMazeForBothSidesCases() {
        return Stream.of(
                Arguments.of(
                        TestMazeObjects.TRAVERSABLE_BUT_NOT_WITH_EITHER_HAND_ON_WALL,
                        UNTRAVERSABLE_REASON_START_REACHED_TOO_MANY_TIMES
                ),
                Arguments.of(
                        TestMazeObjects.UNTRAVERSABLE_WITH_BLOCKED_START,
                        UNTRAVERSABLE_REASON_FIELD_SURROUNDED
                ),
                Arguments.of(
                        TestMazeObjects.UNTRAVERSABLE_WITH_UNREACHABLE_END,
                        UNTRAVERSABLE_REASON_START_REACHED_TOO_MANY_TIMES
                )
        );
    }

    static Stream<Arguments> testTraverseMaze_WithUntraversableMazeCases(HandOnWall.HandOnWallSide side) {
        return Stream.concat(
                testTraverseMaze_WithUntraversableMazeForBothSidesCases(),
                Stream.of(
                        Arguments.of(
                                side == LEFT_SIDE
                                        ? TestMazeObjects.TRAVERSABLE_BUT_NOT_WITH_LEFT_HAND_ON_WALL
                                        : TestMazeObjects.TRAVERSABLE_BUT_NOT_WITH_RIGHT_HAND_ON_WALL,
                                UNTRAVERSABLE_REASON_START_REACHED_TOO_MANY_TIMES
                        )
                )
        );
    }

    @Nested
    public class LeftHandOnWallTest extends AbstractMazeTraversalStrategyTest<HandOnWall> {
        @Override
        protected HandOnWall createMazeTraversalStrategy() {
            return new HandOnWall(LEFT_SIDE);
        }

        @Override
        protected Stream<Arguments> testTraverseMaze_WithTraversableMazeCases() {
            boolean[][][] expectedTraversedBoards = new boolean[][][] {
                    // TRAVERSABLE_WITH_3_PATHS_OF_DIFFERENT_LENGTH
                    new boolean[][] {
                            { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false },
                            { false, false, false, false, false, true , true , true , true , true , true , true , false, true , false },
                            { false, false, false, false, false, false, false, false, false, false, false, true , false, true , false },
                            { false, false, false, false, false, false, false, true , true , true , true , true , false, true , false },
                            { false, false, false, false, false, false, false, true , false, false, false, true , false, true , false },
                            { false, false, false, false, false, false, false, true , true , true , false, true , true , true , false },
                            { false, false, false, false, false, false, false, true , false, false, false, false, false, false, false },
                            { false, false, false, false, false, false, false, true , true , true , true , true , true , true , false },
                            { false, false, false, false, false, false, false, false, false, false, false, false, false, true , false },
                            { false, false, false, false, false, false, false, false, false, false, false, false, false, true , false },
                            { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false },
                    },
                    // TRAVERSABLE_WITH_2_PATHS_OF_SAME_LENGTH
                    new boolean[][] {
                            { false, false, false, false, false, false, false, false, false, false, false },
                            { false, false, false, false, false, false, false, false, false, false, false },
                            { false, false, false, false, false, false, false, false, false, false, false },
                            { false, false, false, false, false, false, false, false, false, false, false },
                            { false, true , false, false, false, true , false, false, false, false, false },
                            { false, true , false, true , true , true , false, false, false, false, false },
                            { false, true , false, true , false, false, false, false, false, false, false },
                            { false, true , true , true , false, false, false, false, false, false, false },
                            { false, false, false, false, false, false, false, false, false, false, false },
                    },
                    // TRAVERSABLE_BUT_NOT_WITH_RIGHT_HAND_ON_WALL
                    new boolean[][] {
                            { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false },
                            { false, false, false, false, false, false, false, true , true , true , true , true , true , true , true , false },
                            { false, false, false, false, false, false, false, true , false, false, false, true , false, false, false, false },
                            { false, false, false, false, false, false, false, true , false, false, false, true , true , true , true , false },
                            { false, false, false, false, false, false, false, false, false, false, false, false, false, false, true , false },
                            { false, false, false, false, false, false, false, false, false, false, false, false, false, false, true , false },
                            { false, false, false, false, false, false, false, false, false, false, false, false, false, false, true , false },
                            { false, false, false, false, false, true , true , true , true , true , true , true , true , true , true , false },
                            { false, false, false, false, false, false, false, false, false, false, false, false, false, true , true , false },
                    },
                    // TRAVERSABLE_WITH_START_AND_END_SAME
                    new boolean[][] {
                            { false, false, false },
                            { false, true , false },
                            { false, false, false },
                    },
                    // TRAVERSABLE_WITH_START_AND_END_NEXT_TO_EACH_OTHER
                    new boolean[][] {
                            { true , true , false },
                            { true , true , false },
                            { false, false, false },
                    }
            };

            return HandOnWallTest.testTraverseMaze_WithTraversableMazeCases(LEFT_SIDE, expectedTraversedBoards);
        }

        @Override
        protected Stream<Arguments> testTraverseMaze_WithUntraversableMazeCases() {
            return HandOnWallTest.testTraverseMaze_WithUntraversableMazeCases(LEFT_SIDE);
        }
    }

    @Nested
    public class RightHandOnWallTest extends AbstractMazeTraversalStrategyTest<HandOnWall> {
        @Override
        protected HandOnWall createMazeTraversalStrategy() {
            return new HandOnWall(RIGHT_SIDE);
        }

        @Override
        protected Stream<Arguments> testTraverseMaze_WithTraversableMazeCases() {
            boolean[][][] expectedTraversedBoards = new boolean[][][] {
                    // TRAVERSABLE_WITH_3_PATHS_OF_DIFFERENT_LENGTH
                    new boolean[][] {
                            { true , true , false, false, false, false, false, false, false, false, false, false, false, false, false },
                            { true , true , true , true , true , true , false, false, false, false, false, false, false, false, false },
                            { false, true , false, false, false, false, false, false, false, false, false, false, false, false, false },
                            { false, true , false, false, false, false, false, false, false, false, false, false, false, false, false },
                            { false, true , false, false, false, false, false, false, false, false, false, false, false, false, false },
                            { false, true , false, false, false, false, false, false, false, false, false, false, false, false, false },
                            { false, true , false, false, false, false, false, false, false, false, false, false, false, false, false },
                            { false, true , false, true , true , true , false, false, false, false, false, true , true , true , false },
                            { false, true , false, true , false, true , false, false, false, false, false, true , false, true , false },
                            { false, true , true , true , false, true , true , true , true , true , true , true , false, true , false },
                            { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false },
                    },
                    // TRAVERSABLE_WITH_2_PATHS_OF_SAME_LENGTH
                    new boolean[][] {
                            { false, false, false, false, false, false, false, false, false, false, false },
                            { false, false, false, false, false, false, false, false, false, false, false },
                            { false, false, false, false, false, false, false, false, false, false, false },
                            { false, false, false, false, false, false, false, false, false, false, false },
                            { false, true , false, false, false, true , false, false, false, false, false },
                            { false, true , false, true , true , true , false, false, false, false, false },
                            { false, true , false, true , false, false, false, false, false, false, false },
                            { false, true , true , true , true , true , true , true , false, false, false },
                            { false, false, false, false, false, false, false, false, false, false, false },
                    },
                    // TRAVERSABLE_BUT_NOT_WITH_LEFT_HAND_ON_WALL
                    new boolean[][] {
                            { true , true , true , false, false, false, false, false, false, false, false, false, false, false, false, false },
                            { true , true , true , true , true , true , true , true , false, false, false, false, false, false, false, false },
                            { false, false, false, false, true , false, false, true , false, false, false, false, false, false, false, false },
                            { false, true , true , true , true , false, false, true , true , true , true , true , true , true , false, false },
                            { false, false, false, false, true , false, false, false, true , false, false, false, false, true , false, false },
                            { false, true , true , true , true , true , true , true , true , false, true , true , false, true , false, false },
                            { false, true , true , false, false, false, false, false, false, false, true , true , false, true , false, false },
                            { false, true , true , false, false, true , true , true , true , true , true , true , true , true , false, false },
                            { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false },
                    },
                    // TRAVERSABLE_WITH_START_AND_END_SAME
                    new boolean[][] {
                            { false, false, false },
                            { false, true , false },
                            { false, false, false },
                    },
                    // TRAVERSABLE_WITH_START_AND_END_NEXT_TO_EACH_OTHER
                    new boolean[][] {
                            { false, false, false },
                            { true , true , false },
                            { false, false, false },
                    }
            };

            return HandOnWallTest.testTraverseMaze_WithTraversableMazeCases(RIGHT_SIDE, expectedTraversedBoards);
        }

        @Override
        protected Stream<Arguments> testTraverseMaze_WithUntraversableMazeCases() {
            return HandOnWallTest.testTraverseMaze_WithUntraversableMazeCases(RIGHT_SIDE);
        }
    }
}
