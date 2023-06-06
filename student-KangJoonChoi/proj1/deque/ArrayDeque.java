package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T>{
    private int size;
    private T[] items;
    public ArrayDeque(){
        size = 0;
        items = (T[]) new Object[8];
    }
    private void resizeaf(int capacity){
        T[] a = (T[]) new Object[capacity];
        System.arraycopy(items,0,a,1,size);
        items = a;
    }
    private void resizeal(int capacity){
        T[] a = (T[]) new Object[capacity];
        System.arraycopy(items,0,a,0,size);
        items = a;
    }
    private void reductionresizerf(int capacity){
        T[] a = (T[]) new Object[capacity*2];
        System.arraycopy(items, 1,a,0,size-1);
        items = a;
    }
    private void reductionresizerl(int capacity){
        T[] a = (T[]) new Object[capacity*2];
        System.arraycopy(items, 0,a,0,size-1);
        items = a;
    }
    public void addFirst(T item){
        if(size == items.length){
            resizeaf(size*2);
        }
        System.arraycopy(items, 0,items,1,size);
        items[0] = item;
        size += 1;
    }

    public void addLast(T item){
        if(size == items.length){
            resizeal(size*2);
        }
        items[size] = item;
        size += 1;
    }

    public boolean isEmpty(){
        return size == 0;
    }
    public int size(){
        return size;
    }
    public void printDeque(){
        int i;
        for(i=0; i<size-1; i++){
            System.out.print(items[i]);
            System.out.print(' ');
        }
        System.out.println(items[i]);
    }
    public T removeFirst(){
        if (size == 0) return null;
        else if (size + 1 < (items.length)/4) {
            T returnval = items[0];
            reductionresizerf((items.length)/4);
            size-=1;
            return returnval;
        }
        T returnval = items[0];
        int j;
        for( j = 1; j < size; j++){
            items[j-1] = items[j];
        }
        items[j-1] = null;
        size -= 1;
        return returnval;
    }
    public T removeLast(){
        if (size == 0) return null;
        else if (size + 1 < (items.length)/4) {
            T returnval = items[size-1];
            reductionresizerl((items.length)/4);
            size-=1;
            return returnval;
        }
        T returnval = items[size-1];
        items[size-1] = null;
        size -= 1;
        return returnval;
    }
    public T get(int index){
        if(index>=size || index<0)return null;
        else return items[index];
    }
    public Iterator<T> iterator(){
        return new arrayIterator();
    }
    private class arrayIterator implements Iterator<T>{
        private int Position;
        public arrayIterator(){
            Position = 0;
        }
        public boolean hasNext(){
            return Position < size;
        }
        public T next(){
            T returnItem = items[Position];
            Position += 1;
            return returnItem;
        }
    }

    public boolean equals(Object o){
        return o instanceof ArrayDeque;
    }

}
