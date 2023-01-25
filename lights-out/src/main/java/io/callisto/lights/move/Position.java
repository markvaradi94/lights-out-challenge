package io.callisto.lights.move;

import lombok.Builder;
import lombok.With;

@With
@Builder(toBuilder = true)
public record Position(
        int column,
        int row
) {
    public String toString() {
        return + column + "," + row;
    }
}
