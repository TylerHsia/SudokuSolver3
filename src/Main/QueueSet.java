package Main;

import jdk.jshell.spi.ExecutionControl;

import java.util.Queue;
import java.util.*;

/**
 * A class which represents a queue of items, which has the set property that no two items
 * in the queue are equal. If an equal item is added to the queue, nothing happens.
 * @param <E> The type of elements of the QueueSet
 */
public class QueueSet<E> implements Queue<E> {
    private Set<E> elementSet;
    private Queue<E> elementQueue;

    public QueueSet(){
        elementSet = new HashSet<>(81);
        elementQueue = new LinkedList<>();
    }


    @Override
    public int size() {
        return elementQueue.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        return elementSet.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<>(){
            Iterator<E> itr = elementQueue.iterator();
            @Override
            public boolean hasNext() {
                return itr.hasNext();
            }

            @Override
            public E next() {
                return itr.next();
            }
        };
    }

    @Override
    public Object[] toArray() {
        return elementQueue.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(E val) {
        if(elementSet.add(val)){
            elementQueue.add(val);
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean changed = false;
        for(E val : c){
            changed |= add(val);
        }
        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean offer(E val) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E remove() {
        E removed = elementQueue.remove();
        elementSet.remove(removed);
        return removed;
    }

    @Override
    public E poll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public E element() {
        throw new UnsupportedOperationException();
    }

    @Override
    public E peek() {
        return elementQueue.peek();
    }
}
