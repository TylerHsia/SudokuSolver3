package Main;

import jdk.jshell.spi.ExecutionControl;

import java.util.Queue;
import java.util.*;

//Todo: make generic
public class QueueSet implements Queue<Integer> {
    private Set<Integer> elementSet;
    private Queue<Integer> elementQueue;

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
    public Iterator<Integer> iterator() {
        return elementQueue.iterator();
    }

    @Override
    public Object[] toArray() {
        return elementQueue.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new RuntimeException("Not Implemented.");
    }

    @Override
    public boolean add(Integer integer) {
        if(elementSet.add(integer)){
            elementQueue.add(integer);
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Integer> c) {
        boolean changed = false;
        for(Integer integer : c){
            if(add(integer)){
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {
        elementSet = new HashSet<>(81);
        elementQueue = new LinkedList<>();
    }

    @Override
    public boolean offer(Integer integer) {
        return false;
    }

    @Override
    public Integer remove() {
        int removed = elementQueue.remove();
        elementSet.remove(removed);
        return removed;
    }

    @Override
    public Integer poll() {
        return null;
    }

    @Override
    public Integer element() {
        return null;
    }

    @Override
    public Integer peek() {
        return elementQueue.peek();
    }
}
