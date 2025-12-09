package com.app;

import com.mazedata.Maze;
import com.mazedata.MazeRepository;
import com.strategies.*;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

/**
 * Interactive console application demonstrating the GoF Strategy Pattern by allowing
 * users to select and apply different maze-solving algorithms at runtime.
 */
public class InteractiveMazeApp {

    private static final String BORDER =
            "═══════════════════════════════════════════════════════════";
    private static final String BORDER_WITH_LINEBREAK =
            System.lineSeparator() +
            "═══════════════════════════════════════════════════════════";

    private final MazeRepository repository;
    private final Scanner scanner;


    // Available strategies
    private final MazeTraversalStrategy[] strategies;
    private final String[] strategyNames;

    public InteractiveMazeApp(MazeRepository repository) {
        this.repository = repository;
        this.scanner = new Scanner(System.in);

        // Initialize strategies
        this.strategies = new MazeTraversalStrategy[] {
                new BreadthFirstSearch(),
                new DepthFirstSearch(),
                new HandOnWall(HandOnWall.HandOnWallSide.RIGHT),
                new HandOnWall(HandOnWall.HandOnWallSide.LEFT)
        };

        this.strategyNames = new String[] {
                "Breadth First Search (Shortest Path)",
                "Depth First Search",
                "Right-Hand on Wall",
                "Left-Hand on Wall"
        };
    }

    /**
     * Starts the interactive maze solving application.
     */
    public void runApplication() {
        displayWelcomeMessage();

        boolean continueRunning = true;

        while (continueRunning) {
            Maze selectedMaze = selectMaze();
            if (selectedMaze == null) {
                System.out.println("No maze selected. Exiting...");
                break;
            }

            MazeTraversalStrategy selectedStrategy = selectStrategy();
            if (selectedStrategy == null) {
                System.out.println("No strategy selected. Exiting...");
                break;
            }

            solveMaze(selectedMaze, selectedStrategy);

            continueRunning = askToContinue();
        }

        displayExitMessage();
        scanner.close();
    }

    /**
     * Displays welcome message and application information.
     */
    private void displayWelcomeMessage() {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         MAZE TRAVERSAL - STRATEGY PATTERN DEMO             ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("This application demonstrates the Strategy Pattern by allowing");
        System.out.println("you to solve mazes using different algorithms.");
        System.out.println();
    }

    /**
     * Displays a list of the available mazes and prompts the user to select one.
     *
     * @return The selected Maze, or null if the user would like to exit the application
     */
    private Maze selectMaze() {
        List<Maze> mazes = repository.getMazes();

        printBorder();
        System.out.println("AVAILABLE MAZES:");
        printBorder();

        IntStream.range(0, mazes.size())
                        .forEach(i -> {
                            Maze maze = mazes.get(i);

                            System.out.printf("%d. %s (%dx%d)%n", i + 1, maze.getDescription(),
                                    maze.getMazeBoardHeight(), maze.getMazeBoardWidth());
                        });

        System.out.println("0. Exit application");
        printBorder();

        int userSelectedInput = getIntegerInput(
                String.format("Select a maze (0-%d): ", mazes.size()), 0, mazes.size()
        );

        if (userSelectedInput == 0) {
            return null;
        }

        Maze selectedMaze = mazes.get(userSelectedInput - 1);
        System.out.println(System.lineSeparator() + "✓ Selected: " + selectedMaze.getDescription());
        System.out.println(System.lineSeparator() + "Maze visualization:");
        System.out.println(selectedMaze.mazeBoardToString());

        return selectedMaze;
    }

    /**
     * Displays the available strategies and prompts the user to select one.
     *
     * @return The selected MazeTraversalStrategy, or null if user wants to exit
     */
    private MazeTraversalStrategy selectStrategy() {
        printBorder();
        System.out.println("AVAILABLE STRATEGIES:");
        printBorder();

        IntStream.range(0, strategyNames.length).forEach(i ->
            System.out.printf("%d. %s%n", i + 1, strategyNames[i])
        );

        System.out.println("0. Exit Application");
        printBorder();

        int userSelectedInput = getIntegerInput(
                String.format("Select a strategy (0-%d): ", strategies.length), 0, strategies.length
        );

        if (userSelectedInput == 0) {
            return null;
        }

        System.out.println(System.lineSeparator() + "✓ Selected: " + strategyNames[userSelectedInput - 1]);
        return strategies[userSelectedInput - 1];
    }

    /**
     * Attempts to solve the given maze using the selected strategy and prints the results
     * over the console.
     *
     * @param maze The maze to solve
     * @param strategy The strategy to use for solving
     */
    private void solveMaze(Maze maze, MazeTraversalStrategy strategy) {
        displayHeaderForTraversal();

        try {
            long startTime = System.nanoTime();
            boolean[][] traversedBoard = strategy.traverseMaze(maze);
            long endTime = System.nanoTime();

            double durationMs = convertToMilliseconds(endTime - startTime);

            displaySuccessfulTraversal(durationMs, maze, traversedBoard);

        } catch (MazeNotTraversableException e) {
            System.out.println(System.lineSeparator() + "✗ MAZE COULD NOT BE SOLVED");
            printBorder();
            System.out.println("Reason: " + e.getReason());
            System.out.println(System.lineSeparator() + "This maze could not be traversed using the selected strategy.");
        }
    }

    private void displayHeaderForTraversal() {
        System.out.println(BORDER_WITH_LINEBREAK);
        System.out.println("SOLVING MAZE...");
        System.out.println(BORDER);
    }

    private void displaySuccessfulTraversal(double durationMs, Maze maze, boolean[][] traversedMazeBoard) {
        System.out.println(System.lineSeparator() + "✓ MAZE SOLVED SUCCESSFULLY!");
        System.out.printf("⏱ Time taken: %.3f ms%n", durationMs);
        System.out.println(System.lineSeparator() + "Solution path (marked with 'x'):");
        System.out.println(BORDER);
        System.out.println(maze.traversedBoardToString(traversedMazeBoard));
        System.out.printf("📊 Path length: %d fields%n", maze.calculateTraversedPathLength(traversedMazeBoard));
    }
    /**
     * Asks the user if they want to solve another maze.
     *
     * @return true if user wants to continue, false otherwise
     */
    private boolean askToContinue() {
        printBorderWithSpacing();

        while (true) {
            System.out.print("Would you like to solve another maze? (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("y")) return true;
            if (input.equals("n")) return false;

            System.out.println("Please enter 'y' for yes or 'n' for no.");
        }
    }

    /**
     * Displays exit message.
     */
    private void displayExitMessage() {
        printBorderWithSpacing();
        System.out.println("Hopefully you enjoyed the demo!");
        printBorder();
    }

    /**
     * Helper method to get validated integer input from user.
     *
     * @param prompt The prompt to display
     * @param min Minimum valid value (inclusive)
     * @param max Maximum valid value (inclusive)
     * @return The validated integer input
     */
    private int getIntegerInput(String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                int input = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (input >= min && input <= max) {
                    return input;
                }

                System.out.printf("Please enter a number between %d and %d.%n", min, max);
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    /**
     * Converts a duration from nanoseconds to milliseconds.
     *
     * @param duration the duration in nanoseconds
     * @return the duration converted to milliseconds
     */
    private double convertToMilliseconds(double duration) {
        return duration / 1_000_000.0;
    }

    private void printBorder() {
        System.out.println(BORDER);
    }

    private void printBorderWithSpacing() {
        System.out.println();
        printBorder();
    }

    /**
     * Main entry point for the interactive maze application.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Initialize repository
        MazeRepository repository = new MazeRepository("src/main/resources/mazes/mazes.txt");
        repository.readMazeFileAndStoreLines();
        repository.importMazes();

        // Check if mazes were loaded
        if (repository.getMazes().isEmpty()) {
            System.out.println("Warning: No mazes could be loaded from the file.");
            System.out.println("Please check that the maze file exists, is properly formatted and is not empty.");
            return;
        }

        // Run the interactive application
        InteractiveMazeApp app = new InteractiveMazeApp(repository);
        app.runApplication();
    }
}