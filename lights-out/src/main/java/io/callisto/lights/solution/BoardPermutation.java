package io.callisto.lights.solution;

import io.callisto.lights.board.VirtualBoard;
import io.callisto.lights.move.PieceMove;
import io.callisto.lights.move.Position;
import lombok.Builder;
import lombok.With;

@With
@Builder(toBuilder = true)
public record BoardPermutation(
        int pieceNumber,
        Position position,
        PieceMove move,
        VirtualBoard initialBoard,
        VirtualBoard modifiedBoard
) {

    public boolean checkPermutationIsCorrectSolution() {
        int[][] modifiedMatrix = modifiedBoard.valueMatrix();
        for (int i = 0; i < modifiedMatrix.length; i++) {
            for (int j = 0; j < modifiedMatrix[i].length; j++) {
                if (modifiedMatrix[i][j] != 0) {
                    return false;
                }
            }
        }
        return true;
    }
}
