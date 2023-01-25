package io.callisto;

import io.callisto.lights.game.Game;
import io.callisto.lights.solution.GameSolver;
import io.callisto.lights.util.InputReader;

public class Main {
    public static void main(String[] args) {
        var reader = new InputReader();
        Game game1 = reader.readGame("src/main/resources/samples/01.txt");
        GameSolver solver1 = new GameSolver(game1);
        solver1.printGameSolution();

        Game game2 = reader.readGame("src/main/resources/samples/02.txt");
        GameSolver solver2 = new GameSolver(game2);
        solver2.printGameSolution();
    }
}
