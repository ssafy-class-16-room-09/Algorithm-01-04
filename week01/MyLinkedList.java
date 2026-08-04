


public class MyLinkedList <T>{
    class Node<T>{
        private T val;
        private Node<T> next;
        private Node<T> prev;

        public Node(T val, Node<T> next, Node<T> prev) {
            this.val = val;
            this.next = next;
            this.prev = prev;
        }

        public Node() {
        }


    }
    
    private Node<T> head;
    private Node<T> tail;
    private int size;
    

    public MyLinkedList() {
        head = new Node<>();
        tail = new Node<>();
        head.next = tail;
        tail.prev = head;
        this.size = 0;
    }

    public MyLinkedList(T... vals) {
        this();
        
        for(T val : vals){
            Node<T> tmp = new Node<>(val, tail, tail.prev);
            tail.prev.next = tmp;
            tail.prev = tmp;
            size++;
        }
    }

    public void addFirst(T val) {
        Node<T> tmp = new Node<>(val, head.next, head);
        head.next.prev = tmp;
        head.next = tmp;
        size++;
    }

    public void addLast(T val) {
        Node<T> tmp = new Node<>(val, tail, tail.prev);
        tail.prev.next = tmp;
        tail.prev = tmp;
        size++;
    }

    public void insertAt(int idx, T val) throws IndexOutOfBoundsException{
        Node<T> find = idx==size ? tail : getNode(idx);
        Node<T> tmp = new Node<>(val, find, find.prev);
        find.prev.next = tmp;
        find.prev = tmp;
        
        size++;
    }

    public boolean removeByValue(T val) {
        Node<T> find = head.next;
        for(; find != tail; find = find.next){
            if(find.val.equals(val)){
                break;
            }
        }

        if(find == tail){
            return false;
        }
        
        find.prev.next = find.next;
        find.next.prev = find.prev;
        size--;

        return true;

    }

    public T removeAt(int idx) throws IndexOutOfBoundsException{
        Node<T> find = getNode(idx);
        
        find.prev.next = find.next;
        find.next.prev = find.prev;
        size--;

        return find.val;
    }

    public T get(int idx) throws IndexOutOfBoundsException{
        return getNode(idx).val;
    }

    public int indexOf(T val){
        Node<T> find = head;
        for(int i=0; i < size; i++){
            find = find.next;
            if(find.val.equals(val)){
                return i;
            }
        }

        return -1;
    }

    public int size(){
        return size;
    }

    private Node<T> getNode(int idx){
        if(idx >= size || idx < 0){
            throw new IndexOutOfBoundsException();
        }
        Node<T> find = head;
        for(int i=0; i <= idx; i++){
            find = find.next;
        }

        return find;
    }

    
 
}

