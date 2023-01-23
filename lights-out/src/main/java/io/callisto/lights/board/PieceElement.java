package io.callisto.lights.board;

import io.callisto.lights.solver.Position;
import lombok.*;

@Data
@Builder
@With
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public final class PieceElement {
    private String value;
    private Position position;

    public String value() {
        return value;
    }

    public Position position() {
        return position;
    }
}
