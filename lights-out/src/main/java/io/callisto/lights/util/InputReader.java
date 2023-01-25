package io.callisto.lights.util;

import io.callisto.lights.board.*;
import io.callisto.lights.game.Game;
import io.callisto.lights.game.GamePiece;
import io.callisto.lights.game.PieceElement;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Collections.singletonList;

public class InputReader {
    private static int PIECE_COUNT = 1;

    public Game readGame(String sourceFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(sourceFile))) {
            String[] nameTokens = sourceFile.split("/");
            String fileName = nameTokens[nameTokens.length - 1];
            List<String> input = reader.lines().toList();
            return buildGame(input, fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Game buildGame(List<String> input, String outputFile) {
        int depth = Integer.parseInt(input.get(0));
        String boardStructure = input.get(1);
        String piecesStructure = input.get(2);

        var board = buildBoardFromStructure(depth, boardStructure);
        var gamePieces = buildGamePiecesFromStructure(piecesStructure);
        return Game.builder()
                .board(board)
                .gamePieces(gamePieces)
                .outputFile(outputFile)
                .build();
    }

    private List<GamePiece> buildGamePiecesFromStructure(String piecesStructure) {
        String[] tokens = piecesStructure.split(" ");
        List<GamePiece> gamePieces = Arrays.stream(tokens)
                .map(this::toGamePiece)
                .toList();
        PIECE_COUNT = 1;
        return gamePieces;
    }

    private GamePiece toGamePiece(String token) {
        String[] pieceRows = token.split(",");
        int rows = pieceRows.length;
        int columns = pieceRows[0].length();
        Map<Integer, List<PieceElement>> piecesMap = new HashMap<>();
        for (int i = 0; i < pieceRows.length; i++) {
            piecesMap.put(i, buildPieceElements(pieceRows[i]));
        }
        return GamePiece.builder()
                .pieceNumber(PIECE_COUNT++)
                .rows(rows)
                .columns(columns)
                .value(piecesMap)
                .build();
    }

    private List<PieceElement> buildPieceElements(String pieceRow) {
        return pieceRow.codePoints()
                .mapToObj(c -> String.valueOf((char) c))
                .map(val -> PieceElement.builder()
                        .value(val)
                        .build())
                .toList();
    }

    private Board buildBoardFromStructure(int depth, String boardStructure) {
        String[] tokens = boardStructure.split(",");
        int rows = tokens.length;
        int columns = tokens[0].length();
        int[][] valueMatrix = new int[rows][columns];
        Map<Integer, List<String>> boardMap = new HashMap<>();
        Map<Integer, List<Integer>> valueMap = new HashMap<>();
        for (int i = 0; i < tokens.length; i++) {
            boardMap.put(i, singletonList(tokens[i]));
            valueMap.put(i, toListOfIntegerValues(boardMap.get(i)));
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                valueMatrix[i][j] = valueMap.get(i).get(j);
            }
        }

        List<BoardElement> boardElements = boardMap.entrySet().stream()
                .map(this::toBoardElements)
                .flatMap(Collection::stream)
                .toList();

        return Board.builder()
                .depth(depth)
                .columns(columns)
                .rows(rows)
                .elements(boardElements)
                .valueMatrix(valueMatrix)
                .build();
    }

    private List<BoardElement> toBoardElements(Map.Entry<Integer, List<String>> entry) {
        AtomicInteger count = new AtomicInteger();
        List<Integer> rowValues = toListOfIntegerValues(entry.getValue());
        return rowValues.stream()
                .map(val -> BoardElement.builder()
                        .row(entry.getKey())
                        .column(count.getAndIncrement())
                        .value(val)
                        .build())
                .toList();
    }

    private static List<Integer> toListOfIntegerValues(List<String> valueString) {
        return valueString.stream()
                .map(String::codePoints)
                .flatMapToInt(val -> val)
                .mapToObj(c -> String.valueOf((char) c))
                .map(Integer::parseInt)
                .toList();
    }
}
