package io.callisto.lights.game;

import io.callisto.lights.board.Board;
import lombok.Builder;
import lombok.With;

import java.util.List;

@With
@Builder(toBuilder = true)
public record Game(
        Board board,
        List<GamePiece> gamePieces,
        String outputFile
) {
    public Game {
        outputFile = "src/main/resources/solutions/" + outputFile;
    }
}
