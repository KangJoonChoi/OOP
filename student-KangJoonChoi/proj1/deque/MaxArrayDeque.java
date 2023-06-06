package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comp;

    public MaxArrayDeque(Comparator<T> c){
        super();
        comp = c;
    }

    public T max(){
        if(this.isEmpty()) return null;
        T maxelement = this.get(0);
        int i = 0;
        while(this.size() > i){
            T temp = this.get(i);
            if(comp.compare(temp, maxelement)>0) maxelement = temp;
            i++;
        }
        return maxelement;
    }

    public T max(Comparator<T> c){
        if(this.isEmpty()) return null;
        T maxelement = this.get(0);
        int i = 0;
        while(this.size() > i){
            T temp = this.get(i);
            if(c.compare(temp, maxelement)>0) maxelement = temp;
            i++;
        }
        return maxelement;
    }
}
