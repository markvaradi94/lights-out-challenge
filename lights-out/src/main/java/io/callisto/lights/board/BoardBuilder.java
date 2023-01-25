package io.callisto.lights.board;

import io.callisto.lights.game.GamePiece;
import io.callisto.lights.game.PieceElement;
import io.callisto.lights.move.PieceMove;
import io.callisto.lights.move.Position;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static io.callisto.lights.move.MoveBuilder.createPossibleMoves;
import static io.callisto.lights.util.GeneralUtils.buildElementsFromMatrix;
import static io.callisto.lights.util.GeneralUtils.buildMatrixFromElements;

public class BoardBuilder {
    public static List<VirtualBoard> createVirtualBoardsForPieceOnBoard(GamePiece piece, VirtualBoard virtualBoard) {
        List<PieceMove> pieceMoves = createPossibleMoves(piece, virtualBoard);

        return pieceMoves.parallelStream()
                .map(BoardBuilder::createVirtualBoardFromMove)
                .toList();
    }

    public static VirtualBoard mapToVirtualBoard(Board board) {
        return VirtualBoard.builder()
                .depth(board.depth())
                .columns(board.columns())
                .rows(board.rows())
                .elements(board.elements())
                .valueMatrix(board.valueMatrix())
                .build();
    }

    public static VirtualBoard applyPieceToBoard(VirtualBoard board, Map<Integer, List<PieceElement>> piece) {
        List<PieceElement> pieceElements = piece.values().stream()
                .flatMap(Collection::stream)
                .toList();

        return addValuesToBoard(board, pieceElements);
    }

    private static VirtualBoard addValuesToBoard(VirtualBoard board, List<PieceElement> pieceElements) {
        List<BoardElement> originalElements = board.elements();
        List<BoardElement> modifiedElements = pieceElements.stream()
                .map(pieceElement -> applyValueToBoardElement(board, pieceElement))
                .toList();

        List<BoardElement> unchangedElements = originalElements.stream()
                .filter(elem -> isUnchangedElement(elem, modifiedElements))
                .toList();

        List<BoardElement> finalElements = Stream.of(unchangedElements, modifiedElements)
                .flatMap(Collection::stream)
                .toList();

        return VirtualBoard.builder()
                .pieceNumber(board.pieceNumber())
                .position(board.position())
                .move(board.move())
                .depth(board.depth())
                .columns(board.columns())
                .rows(board.rows())
                .elements(finalElements)
                .valueMatrix(buildMatrixFromElements(finalElements, board.rows(), board.columns()))
                .build();
    }

    private static boolean isUnchangedElement(BoardElement elem, List<BoardElement> modifiedElements) {
        return modifiedElements.stream()
                .noneMatch(modifiedElem -> elem.column() == modifiedElem.column() && elem.row() == modifiedElem.row());
    }

    private static BoardElement applyValueToBoardElement(VirtualBoard board, PieceElement pieceElement) {
        BoardElement boardElement = findByPosition(board, pieceElement.position());
        int value = boardElement.getValue();

        if (pieceElement.value().equalsIgnoreCase("X")) {
            if (value == board.depth() - 1) {
                return BoardElement.builder()
                        .value(0)
                        .column(boardElement.column())
                        .row(boardElement.row())
                        .build();
            } else {
                return BoardElement.builder()
                        .value(++value)
                        .column(boardElement.column())
                        .row(boardElement.row())
                        .build();
            }
        } else {
            return boardElement;
        }
    }

    private static BoardElement findByPosition(VirtualBoard board, Position position) {
        return board.elements().stream()
                .filter(elem -> elem.row() == position.row() && elem.column() == position.column())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Could not find element for position: " + position));
    }

    private static VirtualBoard createVirtualBoardFromMove(PieceMove move) {
        return VirtualBoard.builder()
                .move(move)
                .pieceNumber(move.pieceNumber())
                .position(move.position())
                .columns(move.result().columns())
                .rows(move.result().rows())
                .elements(buildElementsFromMatrix(move.result().matrix()))
                .valueMatrix(move.result().matrix())
                .build();
    }
}
