package com.app;

import com.mazedata.MazeRepository;

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

        Scanner sc = new Scanner(System.in);
    }
}
