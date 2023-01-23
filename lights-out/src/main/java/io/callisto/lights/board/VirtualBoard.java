package io.callisto.lights.board;

import io.callisto.lights.solver.PieceMove;
import io.callisto.lights.solver.Position;
import lombok.Builder;
import lombok.With;

import static java.util.Optional.ofNullable;

@With
@Builder(toBuilder = true)
public record VirtualBoard(
        int pieceNumber,
        Board board,
        Position position,
        PieceMove move
) {

    public boolean equals(VirtualBoard otherBoard) {
        int[][] matrix1 = board.valueMatrix();
        int[][] matrix2 = ofNullable(otherBoard)
                .map(VirtualBoard::board)
                .map(Board::valueMatrix)
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
