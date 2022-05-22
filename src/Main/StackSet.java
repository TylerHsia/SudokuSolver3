package Main;
import java.util.*;

public class StackSet {
    private Stack<Set<Integer>> stack;
    private Set<Integer> set;
    public StackSet(){
        stack = new Stack<>();
        set = new HashSet<>(9); //Todo: change back to rangeSet
    }

    public void push(Collection<Integer> integers){
        Set<Integer> added = new HashSet<>();
        for(int integer: integers){
            if(set.add(integer)){
                added.add(integer);
            }
        }
        stack.push(added);
    }

    public Set<Integer> pop(){
        Set<Integer> popped = stack.pop();
        set.removeAll(popped);
        return popped;
    }

    public int size(){
        return stack.size();
    }

    public Set<Integer> asSet(){
        return Collections.unmodifiableSet(set);
    }
}
