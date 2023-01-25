package io.callisto.lights.move;

import io.callisto.lights.board.VirtualBoard;
import io.callisto.lights.game.GamePiece;
import io.callisto.lights.game.PieceElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static io.callisto.lights.board.BoardBuilder.applyPieceToBoard;
import static io.callisto.lights.util.GeneralUtils.buildMatrixFromElements;
import static java.util.Comparator.comparingInt;

public class MoveBuilder {
    public static List<PieceMove> createPossibleMoves(GamePiece piece, VirtualBoard board) {
        List<Position> positions = possiblePositions(piece, board);
        return positions.stream()
                .map(pos -> applyPiece(piece, pos, board))
                .toList();
    }

    public static List<Position> boardPositionsToModify(GamePiece piece, Position pos) {
        List<Position> positions = new ArrayList<>();
        int startRow = pos.row();
        int startColumn = pos.column();
        int pieceRows = piece.rows();
        int pieceColumns = piece.columns();

        for (int i = startColumn; i < startColumn + pieceColumns; i++) {
            for (int j = startRow; j < startRow + pieceRows; j++) {
                positions.add(Position.builder()
                        .column(i)
                        .row(j)
                        .build());
            }
        }

        positions.sort(comparingInt(Position::row));
        return positions;
    }

    private static PieceMove applyPiece(GamePiece piece, Position position, VirtualBoard board) {
        List<Position> positionsToModify = boardPositionsToModify(piece, position);
        Map<Integer, List<PieceElement>> pieceStructure = piece.value();
        List<PieceElement> pieceElements = pieceStructure.values().stream()
                .flatMap(Collection::stream)
                .toList();

        for (int i = 0; i < pieceElements.size(); i++) {
            pieceElements.get(i).setPosition(positionsToModify.get(i));
        }

        VirtualBoard newBoard = applyPieceToBoard(board, pieceStructure);
        MoveResult moveResult = MoveResult.builder()
                .rows(newBoard.rows())
                .columns(newBoard.columns())
                .elements(newBoard.elements())
                .matrix(buildMatrixFromElements(newBoard.elements(), newBoard.rows(), newBoard.columns()))
                .build();

        return PieceMove.builder()
                .pieceNumber(piece.pieceNumber())
                .result(moveResult)
                .position(position)
                .build();
    }

    private static List<Position> possiblePositions(GamePiece gamePiece, VirtualBoard board) {
        List<Position> positions = new ArrayList<>();
        int colDiff = board.columns() - gamePiece.columns();
        int rowDiff = board.rows() - gamePiece.rows();

        for (int i = 0; i <= rowDiff; i++) {
            for (int j = 0; j <= colDiff; j++) {
                positions.add(Position.builder()
                        .row(i)
                        .column(j)
                        .build());
            }
        }

        return positions;
    }
}
