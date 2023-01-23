package io.callisto.lights.board;

import lombok.*;

@Data
@With
@EqualsAndHashCode
@ToString
@Builder(toBuilder = true)
public final class BoardElement {
    private final int row;
    private final int column;
    private int value;

    public BoardElement(
            int row,
            int column,
            int value
    ) {
        this.row = row;
        this.column = column;
        this.value = value;
    }

    public int row() {
        return row;
    }

    public int column() {
        return column;
    }

    public int value() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
