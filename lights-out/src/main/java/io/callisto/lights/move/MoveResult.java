package io.callisto.lights.move;

import io.callisto.lights.board.BoardElement;
import lombok.Builder;
import lombok.With;

import java.util.List;

@With
@Builder(toBuilder = true)
public record MoveResult(
        int columns,
        int rows,
        List<BoardElement> elements,
        int[][] matrix
) {
}
