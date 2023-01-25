package io.callisto.lights.util;

import io.callisto.lights.board.BoardElement;

import java.util.ArrayList;
import java.util.List;

public class GeneralUtils {

    public static int[][] buildMatrixFromElements(List<BoardElement> elements, int rows, int columns) {
        int[][] matrix = new int[rows][columns];
        elements.forEach(elem -> matrix[elem.row()][elem.column()] = elem.getValue());
        return matrix;
    }

    public static List<BoardElement> buildElementsFromMatrix(int[][] matrix) {
        List<BoardElement> newElements = new ArrayList<>();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                BoardElement newElement = BoardElement.builder()
                        .row(i)
                        .column(j)
                        .value(matrix[i][j])
                        .build();
                newElements.add(newElement);
            }
        }
        return newElements;
    }
}
