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
import java.util.logging.Logger;
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
    @MethodSource("borderingFieldsCases")
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
    @MethodSource("borderingFieldsCases")
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
    @MethodSource("borderingFieldsCases")
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
    @MethodSource("borderingFieldsCases")
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
    @MethodSource("borderingFieldsCases")
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
    @MethodSource("borderingFieldsCases")
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
    @MethodSource("borderingFieldsCases")
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
    @MethodSource("borderingFieldsCases")
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
    @MethodSource("borderingFieldsCases")
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

    /**
     * A helper method for testing {@link MazeField#determineBorderingFields(int, int,
     * MazeField.BorderingFieldsDirection, MazeField.BorderingFieldSide)}.
     */
    private static void testDetermineBorderingFields(
            MazeField field, MazeField first, MazeField second, MazeField third, MazeField fourth,
            MazeField.BorderingFieldsDirection direction, MazeField.BorderingFieldSide side
    ) {
        List<MazeField> expectedBorderingFields = new ArrayList<>(
                direction == MazeField.BorderingFieldsDirection.CLOCKWISE
                        ? Arrays.asList(first, second, third, fourth)
                        : Arrays.asList(first, fourth, third, second)
        );
        Collections.rotate(expectedBorderingFields, side.ordinal());

        MazeField[] actualBorderingFields = field.determineBorderingFields(
                BOARD_HEIGHT, BOARD_WIDTH, direction, side
        );

        String assertFailInfo = String.join(
                System.lineSeparator(),
                String.format(
                        "Field: %s, Direction: %s, First side: %s.", field, direction, side
                ),
                String.format("Expected: %-35s, actual: %s", first, actualBorderingFields[0]),
                String.format("Expected: %-35s, actual: %s", second, actualBorderingFields[1]),
                String.format("Expected: %-35s, actual: %s", third, actualBorderingFields[2]),
                String.format("Expected: %-35s, actual: %s", fourth, actualBorderingFields[3]),
                System.lineSeparator()
        );

        assertArrayEquals(
                expectedBorderingFields.toArray(), actualBorderingFields,  assertFailInfo
        );
    }

    private static Stream<Arguments> borderingFieldsCases() {
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
    public class DetermineBorderingFieldsIndexesPrivateMethodTest {
        private static final String METHOD_NAME = "determineBorderingFieldsIndexes";
        private Method method;

        @BeforeEach
        void setUp() {
            try {
                method = MazeField.class.getDeclaredMethod(
                        METHOD_NAME,
                        MazeField.BorderingFieldSide.class
                );
            } catch (NoSuchMethodException e) {
                LOGGER.severe(String.format("Cannot find method %s.", METHOD_NAME));
                throw new RuntimeException(e);
            }

            method.setAccessible(true);
        }

        // Test all combinations of BorderingFieldsDirection and BorderingFieldSide
        @Test
        void testDetermineBorderingFieldsIndexes_Top() {
            callDetermineBorderingFieldsIndexesAndAssert(
                    method,
                    MazeField.BorderingFieldSide.TOP,
                    new int[] { 0, 1, 2, 3 }
            );
        }

        @Test
        void testDetermineBorderingFieldsIndexes_Right() {
            callDetermineBorderingFieldsIndexesAndAssert(
                    method,
                    MazeField.BorderingFieldSide.RIGHT,
                    new int[] { 3, 0, 1, 2 }
            );
        }

        @Test
        void testDetermineBorderingFieldsIndexes_Bottom() {
            callDetermineBorderingFieldsIndexesAndAssert(
                    method,
                    MazeField.BorderingFieldSide.BOTTOM,
                    new int[] { 2, 3, 0, 1 }
            );
        }

        @Test
        void testDetermineBorderingFieldsIndexes_Left() {
            callDetermineBorderingFieldsIndexesAndAssert(
                    method,
                    MazeField.BorderingFieldSide.LEFT,
                    new int[] { 1, 2, 3, 0 }
            );
        }

        private static void callDetermineBorderingFieldsIndexesAndAssert(
                Method determineBorderingFieldsIndexesPrivateMethod,
                MazeField.BorderingFieldSide side,
                int[] expectedIndexes
        ) {
            int[] indexes;
            try {
                indexes = (int[]) determineBorderingFieldsIndexesPrivateMethod.invoke(null, side);
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