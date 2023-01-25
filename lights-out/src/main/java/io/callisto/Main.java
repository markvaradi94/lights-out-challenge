package io.callisto;

import io.callisto.lights.game.Game;
import io.callisto.lights.solution.GameSolver;
import io.callisto.lights.util.InputReader;

public class Main {
    public static void main(String[] args) {
        var reader = new InputReader();
        Game game1 = reader.readGame("lights-out/src/main/resources/samples/01.txt");
        GameSolver solver1 = new GameSolver(game1);
        solver1.printGameSolution();
        System.out.println();

        Game game2 = reader.readGame("lights-out/src/main/resources/samples/02.txt");
        GameSolver solver2 = new GameSolver(game2);
        solver2.printGameSolution();

        // the 2nd sample takes a couple of minutes to process, but in the end a correct solution is returned
        // the algorithm could be improved by a batch processing approach, where instead of calculating all the possible
        // moves we take batches for each iteration of a new board for a new game piece, and return the first correct solution
    }
}
