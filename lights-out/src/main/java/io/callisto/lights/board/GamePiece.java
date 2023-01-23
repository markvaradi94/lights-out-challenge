package io.callisto.lights.board;

import lombok.Builder;
import lombok.With;

import java.util.List;
import java.util.Map;

@With
@Builder(toBuilder = true)
public record GamePiece(
        int pieceNumber,
        int rows,
        int columns,
        Map<Integer, List<PieceElement>> value
) {
}
