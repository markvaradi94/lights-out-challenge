package io.callisto.lights.solution;

import io.callisto.lights.board.VirtualBoard;
import io.callisto.lights.game.Game;
import io.callisto.lights.game.GamePiece;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;

import java.util.*;

import static io.callisto.lights.board.BoardBuilder.createVirtualBoardsForPieceOnBoard;
import static io.callisto.lights.board.BoardBuilder.mapToVirtualBoard;
import static io.callisto.lights.util.SolutionWriter.writeSolution;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparingInt;

@Data
@With
@AllArgsConstructor
public class GameSolver {
    private static int SOLUTIONS_COUNT = 1;

    private final Game game;

    Map<Integer, List<PermutationsQueue<BoardPermutation>>> gameMap = new HashMap<>();

    public GameSolver(Game game) {
        this.game = game;
    }

    public void printGameSolution() {
        writeSolution(solution(), game.outputFile());
    }

    private GameSolution solution() {
        gameMap = mapGameMoves();
        List<PermutationsQueue<BoardPermutation>> queues = getLastLevelQueues();
        List<BoardPermutation> lastMoveItems = queues.stream()
                .map(this::getLastMoveFromQueue)
                .toList();

        List<BoardPermutation> completedBoardItems = lastMoveItems.stream()
                .filter(this::checkGameIsFinished)
                .toList();

        List<PermutationsQueue<BoardPermutation>> solutionQueues = completedBoardItems.stream()
                .map(item -> findQueueForCompletedItem(item, queues))
                .toList();

        return solutionQueues.stream()
                .map(this::mapQueueToGameSolution)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Could not solve game board."));
    }

    private GameSolution mapQueueToGameSolution(PermutationsQueue<BoardPermutation> queue) {
        removeInitialBoardPosition(queue);
        List<SolutionItem> solutionItems = queue.getPermutations().stream()
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

    private PermutationsQueue<BoardPermutation> findQueueForCompletedItem(BoardPermutation item, List<PermutationsQueue<BoardPermutation>> queues) {
        return queues.stream()
                .filter(queue -> queue.getPermutations().contains(item))
                .findFirst()
                .orElseThrow();
    }

    private boolean checkGameIsFinished(BoardPermutation item) {
        return item.checkPermutationIsCorrectSolution();
    }

    private BoardPermutation getLastMoveFromQueue(PermutationsQueue<BoardPermutation> queue) {
        return queue.getPermutations().get(game.gamePieces().size());
    }

    private Map<Integer, List<PermutationsQueue<BoardPermutation>>> mapGameMoves() {
        PermutationsQueue<BoardPermutation> permutationsQueue = new PermutationsQueue<>();
        VirtualBoard initialBoard = getInitialBoard();
        BoardPermutation initialBoardState = BoardPermutation.builder()
                .pieceNumber(0)
                .position(null)
                .move(null)
                .initialBoard(initialBoard)
                .modifiedBoard(initialBoard)
                .build();

        permutationsQueue.offer(initialBoardState);
        gameMap.put(0, singletonList(permutationsQueue));
        game.gamePieces().stream()
                .sorted(comparingInt(GamePiece::pieceNumber))
                .forEachOrdered(this::putMovesOnMap);

        return gameMap;
    }

    private void putMovesOnMap(GamePiece piece) {
        if (piece.pieceNumber() == 1) {
            List<VirtualBoard> firstPieceBoards = createVirtualBoardsForPieceOnBoard(piece, getInitialBoard());
            List<BoardPermutation> boardPermutations = firstPieceBoards.stream()
                    .map(board -> BoardPermutation.builder()
                            .pieceNumber(board.pieceNumber())
                            .position(board.position())
                            .move(board.move())
                            .initialBoard(getInitialBoard())
                            .modifiedBoard(board)
                            .build())
                    .toList();

            BoardPermutation firstItem = gameMap.get(0).stream()
                    .findFirst()
                    .map(PermutationsQueue::peek)
                    .orElseThrow(() -> new RuntimeException("Could not find route start"));

            List<PermutationsQueue<BoardPermutation>> permutationsQueues = boardPermutations.stream()
                    .map(item -> createNewQueue(List.of(firstItem, item)))
                    .toList();

            gameMap.put(piece.pieceNumber(), permutationsQueues);
        } else {
            List<VirtualBoard> boardsForNextMove = gameMap.get(piece.pieceNumber() - 1).parallelStream()
                    .flatMap(Collection::stream)
                    .filter(Objects::nonNull)
                    .filter(item -> item.pieceNumber() == piece.pieceNumber() - 1)
                    .map(BoardPermutation::modifiedBoard)
                    .map(board -> board.withDepth(game.board().depth()))
                    .toList();

            List<PermutationsQueue<BoardPermutation>> newQueues = boardsForNextMove.stream()
                    .map(board -> createQueuesForPiece(piece, board))
                    .flatMap(Collection::stream)
                    .toList();

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


    private List<PermutationsQueue<BoardPermutation>> createQueuesForPiece(GamePiece piece, VirtualBoard virtualBoard) {
        List<VirtualBoard> modifiedBoards = createVirtualBoardsForPieceOnBoard(piece, virtualBoard);
        List<BoardPermutation> queueItems = modifiedBoards.parallelStream()
                .map(board -> BoardPermutation.builder()
                        .pieceNumber(board.pieceNumber())
                        .position(board.position())
                        .move(board.move())
                        .initialBoard(virtualBoard)
                        .modifiedBoard(board)
                        .build())
                .toList();

        int solutionsLevel = piece.pieceNumber() - 1;
        List<PermutationsQueue<BoardPermutation>> queues = gameMap.get(solutionsLevel);
        PermutationsQueue<BoardPermutation> newQueue = queues.parallelStream()
                .filter(queue -> queue.getPermutations().get(solutionsLevel).modifiedBoard().equals(virtualBoard))
                .findFirst()
                .orElseThrow();

        return mapItemsToQue(newQueue, queueItems);
    }

    private boolean checkIfQueueIsCorrectSolution(PermutationsQueue<BoardPermutation> queue) {
        if (queue.getPermutations().size() - 1 == game.gamePieces().size()) {
            BoardPermutation lastItem = queue.getPermutations().get(game.gamePieces().size());
            return lastItem.checkPermutationIsCorrectSolution();
        }
        return false;
    }

    private List<PermutationsQueue<BoardPermutation>> mapItemsToQue(PermutationsQueue<BoardPermutation> queue, List<BoardPermutation> queueItems) {
        List<PermutationsQueue<BoardPermutation>> newQueues = new ArrayList<>();

        for (BoardPermutation item : queueItems) {
            PermutationsQueue<BoardPermutation> newQueue = new PermutationsQueue<>();
            for (BoardPermutation next : queue) {
                newQueue.offer(next);
            }
            newQueue.offer(item);
            newQueues.add(newQueue);
        }

        return newQueues;
    }

    private PermutationsQueue<BoardPermutation> createNewQueue(List<BoardPermutation> items) {
        PermutationsQueue<BoardPermutation> queue = new PermutationsQueue<>();
        items.stream()
                .sorted(comparingInt(BoardPermutation::pieceNumber))
                .forEachOrdered(queue::offer);
        return queue;
    }

    private VirtualBoard getInitialBoard() {
        return mapToVirtualBoard(game.board());
    }

    private List<PermutationsQueue<BoardPermutation>> getLastLevelQueues() {
        return gameMap.get(game.gamePieces().size());
    }

    private static void removeInitialBoardPosition(PermutationsQueue<BoardPermutation> queue) {
        queue.poll();
    }
}
