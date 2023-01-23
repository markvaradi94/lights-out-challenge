package io.callisto.lights.board;

import lombok.Builder;
import lombok.With;

import java.util.List;

@With
@Builder(toBuilder = true)
public record GamePieceRow(
        int row,
        List<String> values
) {
}
