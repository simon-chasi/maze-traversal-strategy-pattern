package com.app;

import com.mazedata.MazeRepository;
import com.strategies.HandOnWall;

/**
 *
 */
public class App {
    public static void main( String[] args ) {
        MazeRepository mr = new MazeRepository("src/main/java/com/mazedata/mazes.txt");
        mr.readMazeFileAndStoreLines();
        mr.importMazes(true);
        System.out.println(mr.mazesToString());

        HandOnWall rightHandOnWall = new HandOnWall(HandOnWall.HandOnWallSide.RIGHT);
        HandOnWall leftHandOnWall = new HandOnWall(HandOnWall.HandOnWallSide.LEFT);
        boolean[][] rightHandOnWallTraversedBoard = rightHandOnWall.traverseMaze(mr.getMaze(3));
        boolean[][] leftHandOnWallTraversedBoard = leftHandOnWall.traverseMaze(mr.getMaze(3));
        System.out.println(mr.getMaze(3).traversedBoardToString(rightHandOnWallTraversedBoard));
        System.out.println(mr.getMaze(3).traversedBoardToString(leftHandOnWallTraversedBoard));
    }
}
