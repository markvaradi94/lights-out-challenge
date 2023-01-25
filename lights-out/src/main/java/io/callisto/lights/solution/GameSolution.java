package io.callisto.lights.solution;

import lombok.Builder;
import lombok.With;

import java.util.List;

@With
@Builder(toBuilder = true)
public record GameSolution(
        int solutionCount,
        List<SolutionItem> solutionItems
) {
}
