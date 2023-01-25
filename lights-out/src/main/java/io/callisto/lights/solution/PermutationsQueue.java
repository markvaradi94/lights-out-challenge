package io.callisto.lights.solution;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.LinkedList;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString
public class PermutationsQueue<T> extends AbstractQueue<T> {
    private LinkedList<T> permutations;

    public PermutationsQueue() {
        this.permutations = new LinkedList<T>();
    }

    @Override
    public Iterator<T> iterator() {
        return permutations.iterator();
    }

    @Override
    public int size() {
        return permutations.size();
    }

    @Override
    public boolean offer(T t) {
        if (t == null) return false;
        permutations.add(t);
        return true;
    }

    @Override
    public T poll() {
        Iterator<T> iter = permutations.iterator();
        T t = iter.next();
        if (t != null) {
            iter.remove();
            return t;
        }
        return null;
    }

    @Override
    public T peek() {
        return permutations.getFirst();
    }
}
