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
        return modifiedBoard.board().boardIsComplete();
    }
}
