package io.callisto.lights.solver;

import io.callisto.lights.board.Game;
import io.callisto.lights.board.GamePiece;
import io.callisto.lights.board.VirtualBoard;
import io.callisto.lights.route.QueItem;
import io.callisto.lights.util.GameQueue;
import io.callisto.lights.util.SolutionWriter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;

import java.util.*;

import static java.util.Collections.singletonList;
import static java.util.Comparator.comparingInt;

@Data
@With
@AllArgsConstructor
public class GameSolver {
    private static int SOLUTIONS_COUNT = 1;

    private final Game game;
    private final MoveBuilder moveBuilder;
    private final SolutionWriter solutionWriter = new SolutionWriter();

    Map<Integer, List<GameQueue<QueItem>>> gameMap = new HashMap<>();

    public GameSolver(Game game) {
        this.game = game;
        this.moveBuilder = new MoveBuilder(game);
    }

    public void printGameSolution() {
        solutionWriter.writeSolution(solution(), game.outputFile());
    }

    private GameSolution solution() {
        gameMap = mapGameMoves();
        List<GameQueue<QueItem>> queues = getLastLevelQueues();
        List<QueItem> lastMoveItems = queues.stream()
                .map(this::getLastMoveFromQueue)
                .toList();

        List<QueItem> completedBoardItems = lastMoveItems.parallelStream()
                .filter(this::checkGameIsFinished)
                .toList();

        List<GameQueue<QueItem>> solutionQueues = completedBoardItems.stream()
                .map(item -> findQueueForCompletedItem(item, queues))
                .toList();

        return solutionQueues.stream()
                .map(this::mapQueueToGameSolution)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Could not solve game board."));
    }

    private GameSolution mapQueueToGameSolution(GameQueue<QueItem> queue) {
        removeInitialBoardPosition(queue);
        List<SolutionItem> solutionItems = queue.getMoves().stream()
                .map(item -> SolutionItem.builder()
                        .pieceNumber(item.pieceNumber())
                        .position(item.position())
                        .build())
                .toList();

        return GameSolution.builder()
                .solutionCount(SOLUTIONS_COUNT++)
                .solutionItems(solutionItems)
                .build();
    }

    private static void removeInitialBoardPosition(GameQueue<QueItem> queue) {
        queue.poll();
    }

    private GameQueue<QueItem> findQueueForCompletedItem(QueItem item, List<GameQueue<QueItem>> queues) {
        return queues.stream()
                .filter(queue -> queue.getMoves().contains(item))
                .findFirst()
                .orElseThrow();
    }

    private boolean checkGameIsFinished(QueItem item) {
        return item.checkBoardIsCompleted();
    }

    private QueItem getLastMoveFromQueue(GameQueue<QueItem> queue) {
        return queue.getMoves().get(game.gamePieces().size());
    }

    public Map<Integer, List<GameQueue<QueItem>>> mapGameMoves() {
        GameQueue<QueItem> movesQueues = new GameQueue<>();
        VirtualBoard initialBoard = getInitialBoard();
        QueItem firstItem = QueItem.builder()
                .pieceNumber(0)
                .position(null)
                .move(null)
                .initialBoard(initialBoard)
                .modifiedBoard(initialBoard)
                .initialMatrix(getInitialMatrix())
                .modifiedMatrix(game.board().valueMatrix())
                .build();

        movesQueues.offer(firstItem);
        gameMap.put(0, singletonList(movesQueues));
        game.gamePieces().stream()
                .sorted(comparingInt(GamePiece::pieceNumber))
                .forEachOrdered(this::putMovesOnMap);

        return gameMap;
    }

    private void putMovesOnMap(GamePiece piece) {
        if (piece.pieceNumber() == 1) {
            List<VirtualBoard> firstPieceBoards = moveBuilder.createVirtualBoardsForPieceOnBoard(piece, getInitialBoard());
            List<QueItem> queItems = firstPieceBoards.stream()
                    .map(board -> QueItem.builder()
                            .pieceNumber(board.pieceNumber())
                            .position(board.position())
                            .move(board.move())
                            .initialBoard(getInitialBoard())
                            .modifiedBoard(board)
                            .initialMatrix(getInitialMatrix())
                            .modifiedMatrix(board.board().valueMatrix())
                            .build())
                    .toList();

            QueItem firstItem = gameMap.get(0).stream()
                    .findFirst()
                    .map(GameQueue::peek)
                    .orElseThrow(() -> new RuntimeException("Could not find route start"));

            List<GameQueue<QueItem>> gameQueues = queItems.stream()
                    .map(item -> createNewQueue(List.of(firstItem, item)))
                    .toList();

            gameMap.put(piece.pieceNumber(), gameQueues);
        } else {
            List<VirtualBoard> boardsForNextMove = gameMap.get(piece.pieceNumber() - 1).stream()
                    .flatMap(Collection::stream)
                    .filter(Objects::nonNull)
                    .filter(item -> item.pieceNumber() == piece.pieceNumber() - 1)
                    .map(QueItem::modifiedBoard)
                    .toList();

            List<GameQueue<QueItem>> newQueues = boardsForNextMove.stream()
                    .map(board -> createQueuesForPiece(piece, board))
                    .flatMap(Collection::stream)
                    .toList();

            gameMap.put(piece.pieceNumber(), newQueues);

            if (piece.pieceNumber() == game.gamePieces().size()) {
                newQueues.stream()
                        .filter(this::checkIfQueueIsCorrectSolution)
                        .findFirst()
                        .ifPresent(solvedQueue -> gameMap.put(piece.pieceNumber(), singletonList(solvedQueue)));
            } else {
                gameMap.put(piece.pieceNumber(), newQueues);
            }
        }
    }

    private List<GameQueue<QueItem>> createQueuesForPiece(GamePiece piece, VirtualBoard virtualBoard) {
        List<VirtualBoard> modifiedBoards = moveBuilder.createVirtualBoardsForPieceOnBoard(piece, virtualBoard);
        List<QueItem> queueItems = modifiedBoards.stream()
                .map(board -> QueItem.builder()
                        .pieceNumber(board.pieceNumber())
                        .position(board.position())
                        .move(board.move())
                        .initialBoard(virtualBoard)
                        .modifiedBoard(board)
                        .initialMatrix(virtualBoard.board().valueMatrix())
                        .modifiedMatrix(board.board().valueMatrix())
                        .build())
                .toList();

        int solutionsLevel = piece.pieceNumber() - 1;
        List<GameQueue<QueItem>> queues = gameMap.get(solutionsLevel);
        GameQueue<QueItem> newQueue = queues.stream()
                .filter(queue -> queue.getMoves().get(solutionsLevel).modifiedBoard().equals(virtualBoard))
                .findFirst()
                .orElseThrow();

        return mapItemsToQue(newQueue, queueItems);
    }

    private boolean checkIfQueueIsCorrectSolution(GameQueue<QueItem> queue) {
        if (queue.getMoves().size() - 1 == game.gamePieces().size()) {
            QueItem lastItem = queue.getMoves().get(game.gamePieces().size() - 1);
            return lastItem.checkBoardIsCompleted();
        }
        return false;
    }

    private List<GameQueue<QueItem>> mapItemsToQue(GameQueue<QueItem> queue, List<QueItem> queueItems) {
        List<GameQueue<QueItem>> newQueues = new ArrayList<>();

        for (QueItem item : queueItems) {
            GameQueue<QueItem> newQueue = new GameQueue<>();
            for (QueItem next : queue) {
                newQueue.offer(next);
            }
            newQueue.offer(item);
            newQueues.add(newQueue);
        }

        return newQueues;
    }

    private GameQueue<QueItem> createNewQueue(List<QueItem> items) {
        GameQueue<QueItem> queue = new GameQueue<>();
        items.stream()
                .sorted(comparingInt(QueItem::pieceNumber))
                .forEachOrdered(queue::offer);

        return queue;
    }

    private int[][] getInitialMatrix() {
        return game.board().valueMatrix();
    }

    private VirtualBoard getInitialBoard() {
        return moveBuilder.mapToVirtualBoard(game.board());
    }

    private List<GameQueue<QueItem>> getLastLevelQueues() {
        return gameMap.get(game.gamePieces().size());
    }
}
