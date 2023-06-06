package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private int size;
    private IntNode sentinel;

    public class IntNode{
        public T item;
        public IntNode next;
        public IntNode prev;

        public IntNode(T i, IntNode n, IntNode p){
            item = i;
            next = n;
            prev = p;
        }
    }
    public LinkedListDeque(){
        size = 0;
        sentinel = new IntNode(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;

    }
    public void addFirst(T item){
        size += 1;
        IntNode af = new IntNode(item, sentinel.next, sentinel);
        af.next.prev = af;
        sentinel.next = af;
    }

    public void addLast(T item){
        size += 1;
        IntNode al = new IntNode(item, sentinel, sentinel.prev);
        al.prev.next = al;
        sentinel.prev = al;
    }

    public boolean isEmpty(){
        return size == 0;
    }
    public int size(){
        return size;
    }
    public void printDeque(){
        IntNode currentNode = sentinel.next;
        for(int i = 0; i<size-1; i++){
            System.out.print(currentNode.item);
            System.out.print(' ');
            currentNode = currentNode.next;
        }
        System.out.println(currentNode.item);
    }
    public T removeFirst(){
        if (size == 0) return null;
        size -= 1;
        IntNode currentNode = sentinel.next;
        sentinel.next = currentNode.next;
        currentNode.next.prev = sentinel;
        currentNode.next = null;
        currentNode.prev = null;
        return currentNode.item;
    }
    public T removeLast(){
        if (size == 0) return null;
        size -= 1;
        IntNode currentNode = sentinel.prev;
        sentinel.prev = currentNode.prev;
        currentNode.prev.next = sentinel;
        currentNode.next = null;
        currentNode.prev = null;
        return currentNode.item;
    }
    public T get(int index){
        if(index>=size || index<0)return null;
        Iterator<T> itr = this.iterator();
        int counter = 0;
        T returnvalue = null;
        while(itr.hasNext() && counter<=index){
            returnvalue = itr.next();
            counter++;
        }
        return returnvalue;
    }
    public T getRecursive(int index){
        if(index>=size || index<0)return null;
        int counter = 0;
        IntNode recursivenode = sentinel.next;

        return forcingRecursive(recursivenode, index, counter);
    }
    private T forcingRecursive(IntNode node,int index, int counter){
        if(node.next == null) return null;
        if(index == counter) return node.item;
        return forcingRecursive(node.next, index, counter+1);
    }
    public Iterator<T> iterator(){
        return new linkedListIterator();
    }
    private class linkedListIterator implements Iterator<T>{
        private int Position;
        private IntNode node;
        public linkedListIterator(){
            Position = 0;
            node = sentinel;
        }
        public boolean hasNext(){
            return Position < size;
        }
        public T next(){
            Position += 1;
            node = node.next;
            T returnItem = node.item;
            return returnItem;
        }
    }
    public boolean equals(Object o){
        return o instanceof LinkedListDeque;
    }

}
