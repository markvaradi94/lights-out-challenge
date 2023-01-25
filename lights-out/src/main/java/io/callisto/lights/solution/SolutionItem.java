package io.callisto.lights.solution;

import io.callisto.lights.move.Position;
import lombok.Builder;
import lombok.With;

@With
@Builder(toBuilder = true)
public record SolutionItem(
        int pieceNumber,
        Position position
) {
}
