package io.callisto;

import io.callisto.lights.board.Game;
import io.callisto.lights.solver.GameSolver;
import io.callisto.lights.util.InputReader;

public class Main {
    public static void main(String[] args) {
        var reader = new InputReader();
        Game game = reader.readGame("src/main/resources/samples/test.txt");
        GameSolver solver = new GameSolver(game);
        solver.printGameSolution();
    }
}
