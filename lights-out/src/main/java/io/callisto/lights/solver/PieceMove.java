package io.callisto.lights.solver;

import lombok.Builder;
import lombok.With;

@With
@Builder
public record PieceMove(
        int pieceNumber,
        Position position,
        MoveResult result
) {
}
