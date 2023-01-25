package io.callisto.lights.move;

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
