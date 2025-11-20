package com.mazedata;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class MazeFieldTest {
    /**
     * Logger for tracking events and errors in the {@link MazeFieldTest} class.
     */
    private static final Logger LOGGER = Logger.getLogger(MazeFieldTest.class.getName());

    // used for testing determineBorderingFields
    private static final int BOARD_HEIGHT = 5;
    private static final int BOARD_WIDTH = 5;

    // used for testing borderingFieldSide
    private static final int EXAMPLE_MAZE_FIELD_X = 10;
    private static final int EXAMPLE_MAZE_FIELD_Y = 10;

    /*
        Considering the bordering fields there are 9 types of different fields
        in a board layout which need to be tested:
            1. Top left
            2. Top
            3. Top right
            4. Left
            5. Middle
            6. Right
            7. Bottom left
            8. Bottom
            9. Bottom right

        1#2#3
        #####
        4#5#6
        #####
        7#8#9
     */

    @ParameterizedTest(name = "{index}: direction={0}, side={1}")
    @MethodSource("borderingDirectionsAndSidesCombinations")
    void testDetermineBorderingFields_TopLeftEdgeField(
            MazeField.BorderingFieldsDirection direction, MazeField.BorderingFieldSide side
    ) {
        MazeField field = new MazeField(0, 0);

        MazeField top = null;
        MazeField right = new MazeField(1, 0);
        MazeField bottom = new MazeField(0, 1);
        MazeField left = null;

        testDetermineBorderingFields(
                field, top, right, bottom, left, direction, side
        );
    }

    @ParameterizedTest(name = "{index}: direction={0}, side={1}")
    @MethodSource("borderingDirectionsAndSidesCombinations")
    void testDetermineBorderingFields_TopEdgeField(
            MazeField.BorderingFieldsDirection direction, MazeField.BorderingFieldSide side
    ) {
        int fieldPositionX = BOARD_WIDTH / 2;
        MazeField field = new MazeField(fieldPositionX, 0);

        MazeField top = null;
        MazeField right = new MazeField(fieldPositionX + 1, 0);
        MazeField bottom = new MazeField(fieldPositionX, 1);
        MazeField left = new MazeField(fieldPositionX - 1, 0);

        testDetermineBorderingFields(
                field, top, right, bottom, left, direction, side
        );
    }

    @ParameterizedTest(name = "{index}: direction={0}, side={1}")
    @MethodSource("borderingDirectionsAndSidesCombinations")
    void testDetermineBorderingFields_TopRightEdgeField(
            MazeField.BorderingFieldsDirection direction, MazeField.BorderingFieldSide side
    ) {
        int fieldPositionX = BOARD_WIDTH - 1;
        MazeField field = new MazeField(fieldPositionX, 0);

        MazeField top = null;
        MazeField right = null;
        MazeField bottom = new MazeField(fieldPositionX, 1);
        MazeField left = new MazeField(fieldPositionX - 1, 0);

        testDetermineBorderingFields(
                field, top, right, bottom, left, direction, side
        );
    }

    @ParameterizedTest(name = "{index}: direction={0}, side={1}")
    @MethodSource("borderingDirectionsAndSidesCombinations")
    void testDetermineBorderingFields_LeftEdgeField(
            MazeField.BorderingFieldsDirection direction, MazeField.BorderingFieldSide side
    ) {
        int fieldPositionY = BOARD_HEIGHT / 2;
        MazeField field = new MazeField(0, fieldPositionY);

        MazeField top = new MazeField(0, fieldPositionY - 1);
        MazeField right = new MazeField(1, fieldPositionY);
        MazeField bottom = new MazeField(0, fieldPositionY + 1);
        MazeField left = null;

        testDetermineBorderingFields(
                field, top, right, bottom, left, direction, side
        );
    }

    @ParameterizedTest(name = "{index}: direction={0}, side={1}")
    @MethodSource("borderingDirectionsAndSidesCombinations")
    void testDetermineBorderingFields_MiddleField(
            MazeField.BorderingFieldsDirection direction, MazeField.BorderingFieldSide side
    ) {
        int fieldPositionX = BOARD_WIDTH / 2;
        int fieldPositionY = BOARD_HEIGHT / 2;
        MazeField field = new MazeField(fieldPositionX, fieldPositionY);

        MazeField top = new MazeField(fieldPositionX, fieldPositionY - 1);
        MazeField right = new MazeField(fieldPositionX + 1, fieldPositionY);
        MazeField bottom = new MazeField(fieldPositionX, fieldPositionY + 1);
        MazeField left = new MazeField(fieldPositionX - 1, fieldPositionY);

        testDetermineBorderingFields(
                field, top, right, bottom, left, direction, side
        );
    }

    @ParameterizedTest(name = "{index}: direction={0}, side={1}")
    @MethodSource("borderingDirectionsAndSidesCombinations")
    void testDetermineBorderingFields_RightEdgeField(
            MazeField.BorderingFieldsDirection direction, MazeField.BorderingFieldSide side
    ) {
        int fieldPositionX = BOARD_WIDTH - 1;
        int fieldPositionY = BOARD_HEIGHT / 2;
        MazeField field = new MazeField(fieldPositionX, fieldPositionY);

        MazeField top = new MazeField(fieldPositionX, fieldPositionY - 1);
        MazeField right = null;
        MazeField bottom = new MazeField(fieldPositionX, fieldPositionY + 1);
        MazeField left = new MazeField(fieldPositionX - 1, fieldPositionY);

        testDetermineBorderingFields(
                field, top, right, bottom, left, direction, side
        );
    }

    @ParameterizedTest(name = "{index}: direction={0}, side={1}")
    @MethodSource("borderingDirectionsAndSidesCombinations")
    void testDetermineBorderingFields_BottomLeftEdgeField(
            MazeField.BorderingFieldsDirection direction, MazeField.BorderingFieldSide side
    ) {
        int fieldPositionX = BOARD_WIDTH / 2;
        int fieldPositionY = BOARD_HEIGHT - 1;
        MazeField field = new MazeField(fieldPositionX, fieldPositionY);

        MazeField top = new MazeField(fieldPositionX, fieldPositionY - 1);
        MazeField right = new MazeField(fieldPositionX + 1, fieldPositionY);
        MazeField bottom = null;
        MazeField left = new MazeField(fieldPositionX - 1, fieldPositionY);

        testDetermineBorderingFields(
                field, top, right, bottom, left, direction, side
        );
    }

    @ParameterizedTest(name = "{index}: direction={0}, side={1}")
    @MethodSource("borderingDirectionsAndSidesCombinations")
    void testDetermineBorderingFields_BottomEdgeField(
            MazeField.BorderingFieldsDirection direction, MazeField.BorderingFieldSide side
    ) {
        int fieldPositionX = BOARD_WIDTH / 2;
        int fieldPositionY = BOARD_HEIGHT - 1;
        MazeField field = new MazeField(fieldPositionX, fieldPositionY);

        MazeField top = new MazeField(fieldPositionX, fieldPositionY - 1);
        MazeField right = new MazeField(fieldPositionX + 1, fieldPositionY);
        MazeField bottom = null;
        MazeField left = new MazeField(fieldPositionX - 1, fieldPositionY);

        testDetermineBorderingFields(
                field, top, right, bottom, left, direction, side
        );
    }

    @ParameterizedTest(name = "{index}: direction={0}, side={1}")
    @MethodSource("borderingDirectionsAndSidesCombinations")
    void testDetermineBorderingFields_BottomRightEdgeField(
            MazeField.BorderingFieldsDirection direction, MazeField.BorderingFieldSide side
    ) {
        int fieldPositionX = BOARD_WIDTH - 1;
        int fieldPositionY = BOARD_HEIGHT - 1;
        MazeField field = new MazeField(fieldPositionX, fieldPositionY);

        MazeField top = new MazeField(fieldPositionX, fieldPositionY - 1);
        MazeField right = null;
        MazeField bottom = null;
        MazeField left = new MazeField(fieldPositionX - 1, fieldPositionY);

        testDetermineBorderingFields(
                field, top, right, bottom, left, direction, side
        );
    }

    @ParameterizedTest(name = "{index}: direction={0}, side={1}")
    @MethodSource("borderingDirectionsAndSidesCombinations")
    void testDetermineBorderingFields_FieldOutsideOfBoard(
            MazeField.BorderingFieldsDirection direction, MazeField.BorderingFieldSide side
    ) {
        MazeField field = new MazeField(BOARD_WIDTH, BOARD_HEIGHT);
        assertThrows(
                IllegalArgumentException.class,
                () -> field.determineBorderingFields(
                        BOARD_HEIGHT, BOARD_WIDTH, direction, side
                ),
                "An IllegalArgumentException should have been thrown since "
                        + "the maze field lies outside the given board dimensions."
        );
    }

    /**
     * A helper method for testing {@link MazeField#determineBorderingFields(int, int,
     * MazeField.BorderingFieldsDirection, MazeField.BorderingFieldSide)}.
     */
    private static void testDetermineBorderingFields(
            MazeField field, MazeField top, MazeField right, MazeField bottom, MazeField left,
            MazeField.BorderingFieldsDirection direction, MazeField.BorderingFieldSide side
    ) {
        List<MazeField> expectedBorderingFieldsList = new ArrayList<>(
                direction == MazeField.BorderingFieldsDirection.CLOCKWISE
                        ? Arrays.asList(top, right, bottom, left)
                        : Arrays.asList(top, left, bottom, right)
        );
        Collections.rotate(expectedBorderingFieldsList, side.ordinal() * direction.getDirection());

        MazeField[] actualBorderingFields = field.determineBorderingFields(
                BOARD_HEIGHT, BOARD_WIDTH, direction, side
        );

        String assertFailInfo = String.join(
                System.lineSeparator(),
                String.format(
                        "Field: %s, Direction: %s, First side: %s.", field, direction, side
                ),
                String.format("Expected: %-35s, actual: %s", expectedBorderingFieldsList.get(0), actualBorderingFields[0]),
                String.format("Expected: %-35s, actual: %s", expectedBorderingFieldsList.get(1), actualBorderingFields[1]),
                String.format("Expected: %-35s, actual: %s", expectedBorderingFieldsList.get(2), actualBorderingFields[2]),
                String.format("Expected: %-35s, actual: %s", expectedBorderingFieldsList.get(3), actualBorderingFields[3]),
                System.lineSeparator()
        );

        assertArrayEquals(expectedBorderingFieldsList.toArray(), actualBorderingFields, assertFailInfo);
    }

    static Stream<Arguments> borderingDirectionsAndSidesCombinations() {
        return Stream.of(
                // Clockwise
                Arguments.of(
                        MazeField.BorderingFieldsDirection.CLOCKWISE,
                        MazeField.BorderingFieldSide.TOP
                ),
                Arguments.of(
                        MazeField.BorderingFieldsDirection.CLOCKWISE,
                        MazeField.BorderingFieldSide.RIGHT
                ),
                Arguments.of(
                        MazeField.BorderingFieldsDirection.CLOCKWISE,
                        MazeField.BorderingFieldSide.BOTTOM
                ),
                Arguments.of(
                        MazeField.BorderingFieldsDirection.CLOCKWISE,
                        MazeField.BorderingFieldSide.LEFT
                ),

                // Counterclockwise
                Arguments.of(
                        MazeField.BorderingFieldsDirection.COUNTER_CLOCKWISE,
                        MazeField.BorderingFieldSide.TOP
                ),
                Arguments.of(
                        MazeField.BorderingFieldsDirection.COUNTER_CLOCKWISE,
                        MazeField.BorderingFieldSide.RIGHT
                ),
                Arguments.of(
                        MazeField.BorderingFieldsDirection.COUNTER_CLOCKWISE,
                        MazeField.BorderingFieldSide.BOTTOM
                ),
                Arguments.of(
                        MazeField.BorderingFieldsDirection.COUNTER_CLOCKWISE,
                        MazeField.BorderingFieldSide.LEFT
                )
        );
    }

    @Test
    void testBorderingFieldSide_WithBorderingFields() {
        MazeField field = new MazeField(EXAMPLE_MAZE_FIELD_X, EXAMPLE_MAZE_FIELD_Y);

        // Bordering fields
        MazeField top = new MazeField(EXAMPLE_MAZE_FIELD_X, EXAMPLE_MAZE_FIELD_Y - 1);
        MazeField right = new MazeField(EXAMPLE_MAZE_FIELD_X + 1, EXAMPLE_MAZE_FIELD_Y);
        MazeField bottom = new MazeField(EXAMPLE_MAZE_FIELD_X, EXAMPLE_MAZE_FIELD_Y + 1);
        MazeField left = new MazeField(EXAMPLE_MAZE_FIELD_X - 1, EXAMPLE_MAZE_FIELD_Y);

        assertEquals(MazeField.BorderingFieldSide.TOP, field.borderingFieldSide(top));
        assertEquals(MazeField.BorderingFieldSide.RIGHT, field.borderingFieldSide(right));
        assertEquals(MazeField.BorderingFieldSide.BOTTOM, field.borderingFieldSide(bottom));
        assertEquals(MazeField.BorderingFieldSide.LEFT, field.borderingFieldSide(left));
    }

    @Test
    void testBorderingFieldSide_WithNonBorderingFields() {
        MazeField field = new MazeField(EXAMPLE_MAZE_FIELD_X, EXAMPLE_MAZE_FIELD_Y);

        // Non Bordering fields
        MazeField topRightDiag = new MazeField(EXAMPLE_MAZE_FIELD_X + 1, EXAMPLE_MAZE_FIELD_Y - 1);
        MazeField bottomRightDiag = new MazeField(EXAMPLE_MAZE_FIELD_X + 1, EXAMPLE_MAZE_FIELD_Y + 1);
        MazeField bottomLeftDiag = new MazeField(EXAMPLE_MAZE_FIELD_X - 1, EXAMPLE_MAZE_FIELD_Y + 1);
        MazeField topLeftDiag = new MazeField(EXAMPLE_MAZE_FIELD_X - 1, EXAMPLE_MAZE_FIELD_Y - 1);

        assertThrows(IllegalArgumentException.class, () -> field.borderingFieldSide(topRightDiag));
        assertThrows(IllegalArgumentException.class, () -> field.borderingFieldSide(bottomRightDiag));
        assertThrows(IllegalArgumentException.class, () -> field.borderingFieldSide(bottomLeftDiag));
        assertThrows(IllegalArgumentException.class, () -> field.borderingFieldSide(topLeftDiag));
    }

    @Nested
    public class BorderingFieldDirectionTest {
        @Test
        void testInverseClockwise() {
            assertEquals(
                    MazeField.BorderingFieldsDirection.COUNTER_CLOCKWISE,
                    MazeField.BorderingFieldsDirection.CLOCKWISE.inverse(),
                    "Clockwise inverse should be counterclockwise."
            );
        }

        @Test
        void testInverseCounterClockwise() {
            assertEquals(
                    MazeField.BorderingFieldsDirection.CLOCKWISE,
                    MazeField.BorderingFieldsDirection.COUNTER_CLOCKWISE.inverse(),
                    "Counterclockwise inverse should be clockwise."
            );
        }
    }

    @Nested
    public class BorderingFieldSideTest {

        @ParameterizedTest(name = "{index}: sideToTestOn={0}, distance={1}, direction={2}, expectedSide={3}")
        @MethodSource("nextCases")
        void testNext(
                MazeField.BorderingFieldSide sideToTestOn,
                MazeField.BorderingFieldsDirection direction,
                int distance,
                MazeField.BorderingFieldSide expectedSide
        ) {
                MazeField.BorderingFieldSide actualSide = sideToTestOn.next(direction, distance);
                assertEquals(
                        expectedSide, actualSide,
                        String.format("Expected: %s, actual: %s", expectedSide, actualSide)
                );
        }

        static Stream<Arguments> nextCases() {
            // Map distances of -4 to 4 to each possible direction and
            return borderingDirectionsAndSidesCombinations().flatMap(args -> {
                MazeField.BorderingFieldsDirection direction = (MazeField.BorderingFieldsDirection) args.get()[0];
                MazeField.BorderingFieldSide startingSide = (MazeField.BorderingFieldSide) args.get()[1];

                MazeField.BorderingFieldSide[] order = MazeField.BorderingFieldSide.order(direction);

                int startingIndex = Arrays.asList(order).indexOf(startingSide);

                return IntStream.rangeClosed(-4, 4).mapToObj(n -> {
                    int targetIndex = Math.floorMod(startingIndex + n, order.length);
                    return Arguments.of(startingSide, direction, n, order[targetIndex]);
                });
            });
        }

        @ParameterizedTest(name = "{index}: direction={0}, side={1}, expectedSequences={2}")
        @MethodSource("indexesCases")
        void testIndexes(
                MazeField.BorderingFieldsDirection direction,
                MazeField.BorderingFieldSide side,
                int[] expectedSequences
        ) {
            int[] actualSequences = side.indexes(direction);

            assertArrayEquals(
                    expectedSequences,
                    actualSequences,
                    String.format(
                            "Expected sequences: %s, actual sequences: %s.",
                            Arrays.toString(expectedSequences), Arrays.toString(actualSequences)
                    )
            );
        }

        static Stream<Arguments> indexesCases() {
            AtomicInteger index = new AtomicInteger(0);

            int[][] expectedSequences = new int[][] {
                    new int[] { 0, 1, 2, 3 }, // CLOCKWISE AND TOP
                    new int[] { 3, 0, 1, 2 }, // CLOCKWISE AND RIGHT
                    new int[] { 2, 3, 0, 1 }, // CLOCKWISE AND BOTTOM
                    new int[] { 1, 2, 3, 0 }, // CLOCKWISE AND LEFT
                    new int[] { 0, 3, 2, 1 }, // COUNTER_CLOCKWISE AND TOP
                    new int[] { 1, 0, 3, 2 }, // COUNTER_CLOCKWISE AND RIGHT
                    new int[] { 2, 1, 0, 3 }, // COUNTER_CLOCKWISE AND BOTTOM
                    new int[] { 3, 2, 1, 0 }  // COUNTER_CLOCKWISE AND LEFT
            };

            // Append the respective sequences array to the already existing argument
            return borderingDirectionsAndSidesCombinations().map(arg ->
                    Arguments.of(
                            arg.get()[0],
                            arg.get()[1],
                            expectedSequences[index.getAndIncrement()]
                    )
            );
        }
    }

    @Nested
    public class DetermineBorderingFieldsIndexesPrivateMethodTest {
        private static final String METHOD_NAME = "determineBorderingFieldsIndexes";
        private Method method;

        @BeforeEach
        void setUp() {
            try {
                method = MazeField.class.getDeclaredMethod(
                        METHOD_NAME,
                        MazeField.BorderingFieldsDirection.class,
                        MazeField.BorderingFieldSide.class
                );
            } catch (NoSuchMethodException e) {
                LOGGER.severe(String.format("Cannot find method %s.", METHOD_NAME));
                throw new RuntimeException(e);
            }

            method.setAccessible(true);
        }

        @ParameterizedTest(name = "{index}: direction={0}, side={1}, expectedIndexes={2}")
        @MethodSource("com.mazedata.MazeFieldTest$BorderingFieldSideTest#indexesCases")
        void callDetermineBorderingFieldsIndexesAndAssert(
                MazeField.BorderingFieldsDirection direction,
                MazeField.BorderingFieldSide side,
                int[] expectedIndexes
        ) {
            int[] indexes;
            try {
                indexes = (int[]) method.invoke(null, direction, side);
            } catch (IllegalAccessException | InvocationTargetException e) {
                LOGGER.severe(String.format("Cannot invoke method %s.", METHOD_NAME));
                throw new RuntimeException(e);
            }

            assertArrayEquals(
                    expectedIndexes,
                    indexes,
                    String.format(
                            "Expected indexes: %s, actual indexes: %s.",
                            Arrays.toString(expectedIndexes), Arrays.toString(indexes)
                    )
            );
        }
    }
}