package io.callisto.lights.board;

import lombok.Builder;
import lombok.With;

import java.util.List;

@With
@Builder(toBuilder = true)
public record Board(
        int depth,
        int columns,
        int rows,
        List<BoardElement> elements,
        int[][] valueMatrix
) {
}
