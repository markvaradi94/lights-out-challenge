package io.callisto.lights.board;

import io.callisto.lights.move.PieceMove;
import io.callisto.lights.move.Position;
import lombok.Builder;
import lombok.With;

import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;

@With
@Builder(toBuilder = true)
public record VirtualBoard(
        int pieceNumber,
        Position position,
        PieceMove move,
        int depth,
        int columns,
        int rows,
        List<BoardElement> elements,
        int[][] valueMatrix
) {

    public VirtualBoard {
        elements = ofNullable(elements)
                .map(ArrayList::new)
                .orElseGet(ArrayList::new);
    }

    public boolean equals(VirtualBoard otherBoard) {
        int[][] matrix1 = valueMatrix();
        int[][] matrix2 = ofNullable(otherBoard)
                .map(VirtualBoard::valueMatrix)
                .orElseGet(() -> new int[matrix1.length][matrix1[0].length]);

        for (int i = 0; i < matrix1.length; i++) {
            for (int j = 0; j < matrix1[0].length; j++) {
                if (matrix1[i][j] != matrix2[i][j]) {
                    return false;
                }
            }
        }

        return true;
    }
}
