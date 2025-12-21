package com.strategies;

import com.mazedata.Maze;
import com.mazedata.MazeField;

import java.util.stream.Stream;

/**
 * The class {@code TestMazeObjects} contains {@link Maze} objects for testing a
 * {@link MazeTraversalStrategy} or {@link GuaranteedMazeTraverser}.
 */
public class TestMazeObjects {
    /**
     * A maze which can be solved using any {@link MazeTraversalStrategy}
     * with three paths of different length leading to a solution.
     *
     * <pre>
     * oo#############
     * oooooSoooooo#o#
     * #o###o#####o#o#
     * #o#ooooooooo#o#
     * #o#####o###o#o#
     * #o#o#ooooo#ooo#
     * #o#o###o#######
     * #o#ooo#ooooooo#
     * #o#o#o#####o#o#
     * #ooo#ooooooo#Eo
     * #############o#
     * </pre>
     */
    public static final Maze TRAVERSABLE_WITH_3_PATHS_OF_DIFFERENT_LENGTH = new Maze(
            new boolean[][] {
                    { true , true , false, false, false, false, false, false, false, false, false, false, false, false, false },
                    { true , true , true , true , true , true , true , true , true , true , true , true , false, true , false },
                    { false, true , false, false, false, true , false, false, false, false, false, true , false, true , false },
                    { false, true , false, true , true , true , true , true , true , true , true , true , false, true , false },
                    { false, true , false, false, false, false, false, true , false, false, false, true , false, true , false },
                    { false, true , false, true , false, true , true , true , true , true , false, true , true , true , false },
                    { false, true , false, true , false, false, false, true , false, false, false, false, false, false, false },
                    { false, true , false, true , true , true , false, true , true , true , true , true , true , true , false },
                    { false, true , false, true , false, true , false, false, false, false, false, true , false, true , false },
                    { false, true , true , true , false, true , true , true , true , true , true , true , false, true , true  },
                    { false, false, false, false, false, false, false, false, false, false, false, false, false, true , false },
            },
            new MazeField(5, 1),
            new MazeField(13, 9),
            "A maze which is overall traversable using every strategy with 3 paths of different lengths"
    );

    /**
     * A maze which can be solved using any {@link MazeTraversalStrategy}
     * with two paths of the same length leading to a solution.
     *
     * <pre>
     * oo#########
     * oo#ooooo#o#
     * #o#o#o#o#o#
     * #ooo#o#ooo#
     * #S###E###o#
     * #o#ooo#ooo#
     * #o#o#####o#
     * #ooooooo#o#
     * #########oo
     * </pre>
     */
    public static final Maze TRAVERSABLE_WITH_2_PATHS_OF_SAME_LENGTH = new Maze(
            new boolean[][] {
                    { true , true , false, false, false, false, false, false, false, false, false },
                    { true , true , false, true , true , true , true , true , false, true , false },
                    { false, true , false, true , false, true , false, true , false, true , false },
                    { false, true , true , true , false, true , false, true , true , true , false },
                    { false, true , false, false, false, true , false, false, false, true , false },
                    { false, true , false, true , true , true , false, true , true , true , false },
                    { false, true , false, true , false, false, false, false, false, true , false },
                    { false, true , true , true , true , true , true , true , false, true , false },
                    { false, false, false, false, false, false, false, false, false, true , true  },
            },
            new MazeField(1, 4),
            new MazeField(5, 4),
            "A maze which is overall traversable using every strategy with 2 paths of the same length"
    );

    /**
     * An overall traversable maze which is not traversable using {@link HandOnWall}.
     * The maze follower using the hand on wall (side doesn't matter) traversal strategy
     * should run into a loop already at the first step.
     *
     * <pre>
     * ooo##########
     * oooooooooooo#
     * #ooSooo###oo#
     * #oooooooo#oo#
     * #oo#######oo#
     * #Eoooooooooo#
     * #oo#oo####oo#
     * #oo#oo#oo####
     * ##########oo#
     * </pre>
     */
    public static final Maze TRAVERSABLE_BUT_NOT_WITH_EITHER_HAND_ON_WALL = new Maze(
            new boolean[][] {
                    { true , true , true , false, false, false, false, false, false, false, false, false, false },
                    { true , true , true , true , true , true , true , true , true , true , true , true , false },
                    { false, true , true , true , true , true , true , false, false, false, true , true , false },
                    { false, true , true , true , true , true , true , true , true , false, true , true , false },
                    { false, true , true , false, false, false, false, false, false, false, true , true , false },
                    { false, true , true , true , true , true , true , true , true , true , true , true , false },
                    { false, true , true , false, true , true , false, false, false, false, true , true , false },
                    { false, true , true , false, true , true , false, true , true , false, false, false, false },
                    { false, false, false, false, false, false, false, false, false, false, true , true , false },
            },
            new MazeField(3, 2),
            new MazeField(1, 5),
            "An overall traversable maze which is not traversable using "
                    + "the hand on wall (left or right) traversal strategy"
    );

    /**
     * An overall traversable maze which is not traversable using left {@link HandOnWall}.
     * The maze follower using the left-hand on wall traversal strategy should run into a loop already
     * at the first step while a right-hand on wall follower would detect a wall on the right side
     * at the first step and move on.
     *
     * <pre>
     * ooo#############
     * oooooooo#oooooo#
     * ####oooo#ooo####
     * #ooooooSooooooo#
     * ####ooooo####oo#
     * #oooooooo#oo#oo#
     * #oo#######oo#oo#
     * #oo#oEooooooooo#
     * #############oo#
     * </pre>
     */
    public static final Maze TRAVERSABLE_BUT_NOT_WITH_LEFT_HAND_ON_WALL = new Maze(
            new boolean[][] {
                    { true , true , true , false, false, false, false, false, false, false, false, false, false, false, false, false },
                    { true , true , true , true , true , true , true , true , false, true , true , true , true , true , true , false },
                    { false, false, false, false, true , true , true , true , false, true , true , true , false, false, false, false },
                    { false, true , true , true , true , true , true , true , true , true , true , true , true , true , true , false },
                    { false, false, false, false, true , true , true , true , true , false, false, false, false, true , true , false },
                    { false, true , true , true , true , true , true , true , true , false, true , true , false, true , true , false },
                    { false, true , true , false, false, false, false, false, false, false, true , true , false, true , true , false },
                    { false, true , true , false, true , true , true , true , true , true , true , true , true , true , true , false },
                    { false, false, false, false, false, false, false, false, false, false, false, false, false, true , true , false },
            },
            new MazeField(7, 3),
            new MazeField(5, 7),
            "An overall traversable maze which is not traversable using "
                    + "the left-hand on wall traversal strategy"
    );

    /**
     * An overall traversable maze which is not traversable using right {@link HandOnWall}.
     * The maze follower using the right-hand on wall traversal strategy should run into a loop already
     * at the first step while a left-hand on wall follower would detect a wall on the left side
     * at the first step and move on.
     *
     * <pre>
     * ooo#############
     * oooooo#oooooooo#
     * ####oo#ooooo####
     * #ooooooSooooooo#
     * ####ooooo####oo#
     * #oooooooo#oo#oo#
     * #oo#######oo#oo#
     * #oo#oEooooooooo#
     * #############oo#
     * </pre>
     */
    public static final Maze TRAVERSABLE_BUT_NOT_WITH_RIGHT_HAND_ON_WALL = new Maze(
            new boolean[][] {
                    { true , true , true , false, false, false, false, false, false, false, false, false, false, false, false, false },
                    { true , true , true , true , true , true , false, true , true , true , true , true , true , true , true , false },
                    { false, false, false, false, true , true , false, true , true , true , true , true , false, false, false, false },
                    { false, true , true , true , true , true , true , true , true , true , true , true , true , true , true , false },
                    { false, false, false, false, true , true , true , true , true , false, false, false, false, true , true , false },
                    { false, true , true , true , true , true , true , true , true , false, true , true , false, true , true , false },
                    { false, true , true , false, false, false, false, false, false, false, true , true , false, true , true , false },
                    { false, true , true , false, true , true , true , true , true , true , true , true , true , true , true , false },
                    { false, false, false, false, false, false, false, false, false, false, false, false, false, true , true , false },
            },
            new MazeField(7, 3),
            new MazeField(5, 7),
            "An overall traversable maze which is not traversable using "
                    + "the right-hand on wall traversal strategy"
    );

    /**
     * A simple traversable maze with a starting and ending field sharing position.
     *
     * <pre>
     * oo#
     * oS#
     * #o#
     * </pre>
     */
    public static final Maze TRAVERSABLE_WITH_START_AND_END_SAME = new Maze(
            new boolean[][] {
                    { true , true , false },
                    { true , true , false },
                    { false, true , false },
            },
            new MazeField(1, 1),
            new MazeField(1, 1),
            "A simple traversable maze with a starting and ending field sharing position"
    );

    /**
     * A simple traversable maze with the starting and ending field positioned next to each other.
     *
     * <pre>
     * oo#
     * SE#
     * #o#
     * </pre>
     */
    public static final Maze TRAVERSABLE_WITH_START_AND_END_NEXT_TO_EACH_OTHER = new Maze(
            new boolean[][] {
                    { true , true , false },
                    { true , true , false },
                    { false, true , false },
            },
            new MazeField(0, 1),
            new MazeField(1, 1),
            "A simple traversable maze with the starting and ending field positioned next to each other"
    );

    /**
     * An untraversable maze with a starting field blocked by 4 walls.
     *
     * <pre>
     * ooo#######
     * oooo#S#oo#
     * #oo####oo#
     * #oo#Eoooo#
     * #######oo#
     * </pre>
     */
    public static final Maze UNTRAVERSABLE_WITH_BLOCKED_START = new Maze(
            new boolean[][] {
                    { true , true , true , false, false, false, false, false, false, false },
                    { true , true , true , true , false, true , false, true , true , false },
                    { false, true , true , false, false, false, false, true , true , false },
                    { false, true , true , false, true , true , true , true , true , false },
                    { false, false, false, false, false, false, false, true , true , false },
            },
            new MazeField(5, 1),
            new MazeField(4, 3),
            "An untraversable maze with a blocked starting field"
    );

    /**
     * An untraversable maze with an unreachable ending field.
     *
     * <pre>
     * ooo##########
     * oo#ooE#ooooo#
     * #oo#o##oo#oo#
     * #oo##oooo#oo#
     * ####oo#oo####
     * #ooooo#oo#oo#
     * #oo#######oo#
     * #ooooooSoooo#
     * ##########oo#
     * </pre>
     */
    public static final Maze UNTRAVERSABLE_WITH_UNREACHABLE_END = new Maze(
            new boolean[][] {
                    { true , true , true , false, false, false, false, false, false, false, false, false, false },
                    { true , true , false, true , true , true , false, true , true , true , true , true , false },
                    { false, true , true , false, true , false, false, true , true , false, true , true , false },
                    { false, true , true , false, false, true , true , true , true , false, true , true , false },
                    { false, false, false, false, true , true , false, true , true , false, false, false, false },
                    { false, true , true , true , true , true , false, true , true , false, true , true , false },
                    { false, true , true , false, false, false, false, false, false, false, true , true , false },
                    { false, true , true , true , true , true , true , true , true , true , true , true , false },
                    { false, false, false, false, false, false, false, false, false, false, true , true , false },
            },
            new MazeField(7, 7),
            new MazeField(5, 1),
            "An untraversable maze with an unreachable ending field"
    );

    private TestMazeObjects() {}

    public static Stream<Maze> getTraversableMazes() {
        return Stream.of(
                TRAVERSABLE_WITH_3_PATHS_OF_DIFFERENT_LENGTH,
                TRAVERSABLE_WITH_2_PATHS_OF_SAME_LENGTH,
                TRAVERSABLE_BUT_NOT_WITH_EITHER_HAND_ON_WALL,
                TRAVERSABLE_BUT_NOT_WITH_LEFT_HAND_ON_WALL,
                TRAVERSABLE_BUT_NOT_WITH_RIGHT_HAND_ON_WALL,
                TRAVERSABLE_WITH_START_AND_END_SAME,
                TRAVERSABLE_WITH_START_AND_END_NEXT_TO_EACH_OTHER
        );
    }

    public static Stream<Maze> getUntraversableMazes() {
        return Stream.of(
                UNTRAVERSABLE_WITH_BLOCKED_START,
                UNTRAVERSABLE_WITH_UNREACHABLE_END
        );
    }
}
