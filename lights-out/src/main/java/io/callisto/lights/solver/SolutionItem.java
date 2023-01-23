package io.callisto.lights.solver;

import lombok.Builder;
import lombok.With;

@With
@Builder(toBuilder = true)
public record SolutionItem(
        int pieceNumber,
        Position position
) {
}
