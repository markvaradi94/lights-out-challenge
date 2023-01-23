package io.callisto.lights.route;

import io.callisto.lights.board.VirtualBoard;
import io.callisto.lights.solver.PieceMove;
import io.callisto.lights.solver.Position;
import lombok.Builder;
import lombok.With;

@With
@Builder(toBuilder = true)
public record QueItem(
        int pieceNumber,
        Position position,
        PieceMove move,
        VirtualBoard initialBoard,
        VirtualBoard modifiedBoard,
        int[][] initialMatrix,
        int[][] modifiedMatrix
) {

    public boolean checkBoardIsCompleted() {
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
