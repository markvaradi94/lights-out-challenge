package io.callisto.lights.util;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.LinkedList;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString
public class GameQueue<T> extends AbstractQueue<T> {
    private LinkedList<T> moves;

    public GameQueue() {
        this.moves = new LinkedList<T>();
    }

    @Override
    public Iterator<T> iterator() {
        return moves.iterator();
    }

    @Override
    public int size() {
        return moves.size();
    }

    @Override
    public boolean offer(T t) {
        if (t == null) return false;
        moves.add(t);
        return true;
    }

    public GameQueue<T> offerAndReturn(T t) {
        if (t == null) return null;
        moves.add(t);
        return this;
    }

    @Override
    public T poll() {
        Iterator<T> iter = moves.iterator();
        T t = iter.next();
        if (t != null) {
            iter.remove();
            return t;
        }
        return null;
    }

    @Override
    public T peek() {
        return moves.getFirst();
    }
}
