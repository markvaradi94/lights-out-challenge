package io.callisto;

import io.callisto.lights.game.Game;
import io.callisto.lights.solution.GameSolver;
import io.callisto.lights.util.InputReader;

public class Main {
    public static void main(String[] args) {
        var reader = new InputReader();
        Game game = reader.readGame("src/main/resources/samples/02.txt");
        GameSolver solver = new GameSolver(game);
        solver.printGameSolution();
//        System.out.println();
//        solver.printGameSolution();
//        System.out.println();
//        solver.printGameSolution();
//        System.out.println();
//        solver.printGameSolution();
//        System.out.println();
//        solver.printGameSolution();
    }
}
