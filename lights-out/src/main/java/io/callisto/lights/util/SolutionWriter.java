package io.callisto.lights.util;

import io.callisto.lights.solver.GameSolution;
import io.callisto.lights.solver.Position;
import io.callisto.lights.solver.SolutionItem;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Collectors;

public class SolutionWriter {
    public void writeSolution(GameSolution solution, String outputFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            String solutionString = solution.solutionItems().stream()
                    .map(SolutionItem::position)
                    .map(Position::toString)
                    .collect(Collectors.joining(" "));

            writer.write(solutionString);
            System.out.println(solutionString);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
