package com.app;

import com.mazedata.MazeRepository;
import com.mazedata.MazeTraversalSolution;
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
        MazeTraversalSolution rightHandOnWallSolution = rightHandOnWall.traverseMaze(mr.getMaze(2));
        MazeTraversalSolution leftHandOnWallSolution = leftHandOnWall.traverseMaze(mr.getMaze(2));
        //System.out.println(rightHandOnWallSolution.toString());
        //System.out.println(leftHandOnWallSolution.toString());
    }
}
