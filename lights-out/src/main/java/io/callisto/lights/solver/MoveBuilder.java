package io.callisto.lights.solver;

import io.callisto.lights.board.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.Comparator.comparingInt;

@Data
@With
@AllArgsConstructor
public class MoveBuilder {
    private final Game game;
    private final List<PieceMove> moves = new ArrayList<>();

    public List<VirtualBoard> createVirtualBoardsForPieceOnBoard(GamePiece piece, VirtualBoard virtualBoard) {
        List<PieceMove> pieceMoves = createPossibleMoves(piece, virtualBoard.board());

        return pieceMoves.stream()
                .map(move -> VirtualBoard.builder()
                        .move(move)
                        .position(move.position())
                        .pieceNumber(move.pieceNumber())
                        .board(createBoardFromMove(move))
                        .build())
                .toList();
    }

    public VirtualBoard mapToVirtualBoard(Board board) {
        return VirtualBoard.builder()
                .board(board)
                .build();
    }

    private Board createBoardFromMove(PieceMove move) {
        return createBoardFromMatrix(move.result().matrix());
    }

    public Board createBoardFromMatrix(int[][] matrix) {
        Board newBoard = Board.builder()
                .depth(game.board().depth())
                .rows(game.board().rows())
                .columns(game.board().columns())
                .elements(new ArrayList<>())
                .valueMatrix(matrix)
                .build();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                newBoard.elements().add(BoardElement.builder()
                        .row(i)
                        .column(j)
                        .value(matrix[i][j])
                        .build());
            }
        }
        return newBoard;
    }

    public List<PieceMove> createPossibleMoves(GamePiece piece, Board board) {
        List<Position> positions = possiblePositions(piece, board);
        return positions.stream()
                .map(pos -> applyPiece(piece, pos, board))
                .toList();
    }

    private PieceMove applyPiece(GamePiece piece, Position pos, Board board) {
        board.resetBoard();
        List<Position> positionsToModify = boardPositionsToModify(piece, pos);
        Map<Integer, List<PieceElement>> pieceStructure = piece.value();

        List<PieceElement> pieceElements = pieceStructure.values().stream()
                .flatMap(Collection::stream)
                .toList();

        for (int i = 0; i < pieceElements.size(); i++) {
            pieceElements.get(i).setPosition(positionsToModify.get(i));
        }

        Board newBoard = board.applyPieceOnBoard(piece);
        MoveResult moveResult = MoveResult.builder()
                .rows(newBoard.rows())
                .columns(newBoard.columns())
                .elements(newBoard.elements())
                .matrix(buildMatrixFromElements(newBoard.elements(), newBoard.rows(), newBoard.columns()))
                .build();

        moveResult.updateElements();

        return PieceMove.builder()
                .pieceNumber(piece.pieceNumber())
                .result(moveResult)
                .position(pos)
                .build();
    }

    private int[][] buildMatrixFromElements(List<BoardElement> elements, int rows, int columns) {
        int[][] matrix = new int[rows][columns];
        elements.forEach(elem -> matrix[elem.row()][elem.column()] = elem.getValue());
        return matrix;
    }

    public List<Position> boardPositionsToModify(GamePiece piece, Position pos) {
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

    public List<Position> possiblePositionsForVirtualBoard(GamePiece gamePiece, VirtualBoard virtualBoard) {
        List<Position> positions = new ArrayList<>();
        int colDiff = virtualBoard.board().columns() - gamePiece.columns();
        int rowDiff = virtualBoard.board().rows() - gamePiece.rows();

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

    public List<Position> possiblePositions(GamePiece gamePiece, Board board) {
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
