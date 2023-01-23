package io.callisto.lights.solver;

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
