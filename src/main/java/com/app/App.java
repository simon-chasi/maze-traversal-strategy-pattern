package com.app;

import com.mazedata.MazeRepository;
import com.mazedata.MazeTraversalSolution;
import com.strategies.HandOnWall;

import java.util.Scanner;

/**
 *
 */
public class App {
    public static void main( String[] args ) {
        MazeRepository mr = new MazeRepository("src/main/java/com/mazedata/mazes.txt");
        mr.readMazeFileAndStoreLines();
        mr.importMazes(true);
        System.out.println(mr.mazesToString());

        HandOnWall handOnWall = new HandOnWall(HandOnWall.HandOnWallSide.RIGHT);
        MazeTraversalSolution handOnWallSolution = handOnWall.traverseMaze(mr.getMaze(0));
        System.out.println(handOnWallSolution.toString());
    }
}
