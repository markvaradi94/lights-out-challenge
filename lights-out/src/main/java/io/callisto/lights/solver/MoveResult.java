package io.callisto.lights.solver;

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
    public void updateElements() {
        elements.forEach(elem -> elem.setValue(matrix[elem.row()][elem.column()]));
    }
}
