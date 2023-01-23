package io.callisto.lights.board;

import io.callisto.lights.solver.Position;
import lombok.Builder;
import lombok.With;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@With
@Builder(toBuilder = true)
public record Board(
        int depth,
        int columns,
        int rows,
        List<BoardElement> elements,
        int[][] valueMatrix
) implements Cloneable {

    @Override
    protected Board clone() throws CloneNotSupportedException {
        return (Board) super.clone();
    }

    public Board applyPieceOnBoard(GamePiece piece) {
        return applyPieceToBoard(piece.value());
    }

    public Board applyPieceToBoard(Map<Integer, List<PieceElement>> piece) {
        Board newBoard = copyCurrentBoard();
        piece.values().stream()
                .flatMap(Collection::stream)
                .forEach(elem -> modifyBoard(newBoard, elem));
        return newBoard;
    }

    public void resetBoard() {
        elements.forEach(elem -> elem.setValue(valueMatrix[elem.row()][elem.column()]));
    }

    public void modifyBoard(Board board, PieceElement pieceElement) {
        BoardElement boardElement = findByPosition(board, pieceElement.position());
        int value = boardElement.getValue();

        if (pieceElement.getValue().equalsIgnoreCase("X")) {
            if (value == depth - 1) {
                boardElement.setValue(0);
            } else {
                boardElement.setValue(++value);
            }
        }
    }

    private Board copyCurrentBoard() {
        try {
            return clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException();
        }
    }

    public BoardElement findByPosition(Board board, Position position) {
        return board.elements.stream()
                .filter(elem -> elem.row() == position.row() && elem.column() == position.column())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Could not find element for position: " + position));
    }
}
